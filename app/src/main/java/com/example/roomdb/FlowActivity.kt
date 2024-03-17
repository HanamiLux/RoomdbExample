package com.example.roomdb

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.example.roomdb.databinding.ActivityFlowBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class FlowActivity : AppCompatActivity() {

    lateinit var binding: ActivityFlowBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val db = AppDatabase.getDbInstance(this)

        val login: String = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(R.string.UserLogin.toString(), "").toString() ?: return

        var user = db.usersDao().getUser(login)
        if(user == null) accountExit()

        binding.apply {
            LoginET.setText("${user.login}")
            PwdET.setText("${user.password}")
            getData(user)

            TopUpBtn.setOnClickListener {
                if (CategoryET.text.toString() == "" || MoneyET.text.toString() == "" || MoneyET.text.toString().toDouble() == 0.0) return@setOnClickListener

                db.moneyFlowsDao().insertFlow(
                    MoneyFlow(
                        null,
                        CategoryET.text.toString(),
                        MoneyET.text.toString().toDouble(),
                        user.id
                    )
                )
                getData(user)
            }

            WithdrawBtn.setOnClickListener {
                if (CategoryET.text.toString() == "" || MoneyET.text.toString() == "" || MoneyET.text.toString().toDouble() == 0.0) return@setOnClickListener
                val withdrawAmount = MoneyET.text.toString().toDouble()
                if(withdrawAmount > binding.BalanceTW.text.toString().toDouble()){
                    Toast.makeText(this@FlowActivity, "Денег нема", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                db.moneyFlowsDao().insertFlow(
                    MoneyFlow(
                        null,
                        CategoryET.text.toString(),
                        -withdrawAmount,
                        user.id
                    )
                )
                getData(user)
            }

            ChangeDataBtn.setOnClickListener {
                user.login = LoginET.text.toString()
                user.password = PwdET.text.toString()
                updatePreferences(user.login, user.password)
                db.usersDao().updateUser(user)
                Toast.makeText(this@FlowActivity, "Data has successfully updated!", Toast.LENGTH_SHORT).show()

            }
            ExitBtn.setOnClickListener {
                accountExit()
            }

            // design for every chart
            arrayOf(TopUpPieChart, WithdrawPieChart).forEach { chart ->
                chart.apply {
                    setUsePercentValues(false)
                    setCenterTextColor(Color.WHITE)
                    centerTextRadiusPercent = 0.5f
                    animateY(2000, Easing.EaseInOutBack)
                    description.isEnabled = false
                    legend.textColor = Color.WHITE
                    setEntryLabelTextSize(16f)
                    legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                }
            }
        }

    }

    private fun accountExit() {
        updatePreferences("", "")
        startActivity(Intent(this@FlowActivity, SignInActivity::class.java))
        finish()
    }

    private fun getData(user: User) {
        //updateBalance
        val db = AppDatabase.getDbInstance(applicationContext)
        val moneyFlow = db.moneyFlowsDao().getFlowByUser(user.id.toString())
        var resultAmount: Double = 0.0
        for (flow in moneyFlow) {
            resultAmount += flow.amount
        }
        if (resultAmount <= 0)
            resultAmount = 0.0
        binding.BalanceTW.text = resultAmount.toString()

        // Charts updating
        val topUpEntries: ArrayList<PieEntry> = ArrayList()
        val withdrawEntries: ArrayList<PieEntry> = ArrayList()
        val topUpFlows: ArrayList<FlowData> = ArrayList()
        val withdrawFlows: ArrayList<FlowData> = ArrayList()

        for (flow in moneyFlow) {
            if(flow.amount > 0){ // getting all top ups
                topUpFlows.add(FlowData(flow.category, flow.amount))
            }
            else{ // getting all withdraws
                withdrawFlows.add(FlowData(flow.category, flow.amount))
            }
        }

        //getting unique categories
        val topUpUniqueCategories = topUpFlows.distinctBy { it.name }
        val withdrawUniqueCategories = withdrawFlows.distinctBy { it.name }
        //fetch all top up data to unique with summary money
        for (topUpUniqueCategory in topUpUniqueCategories) {
            var topUpSummary: Double = 0.0
            val resultData = FlowData(topUpUniqueCategory.name, 0.0)
            topUpFlows.forEach {
                topUpSummary += it.amount
                if(it.name == topUpUniqueCategory.name){
                    resultData.amount += it.amount
                }
            } //add result flow data to pie chart
            topUpEntries.add(PieEntry((resultData.amount * 100 / topUpSummary).toFloat(), resultData.name))
        }
        //fetch all withdraw data to unique with summary money
        for (withdrawUniqueCategory in withdrawUniqueCategories) {
            var withdrawSummary: Double = 0.0
            val resultData = FlowData(withdrawUniqueCategory.name, 0.0)
            withdrawFlows.forEach {
                withdrawSummary += it.amount
                if(it.name == withdrawUniqueCategory.name){
                    resultData.amount += it.amount
                }
            }//add result flow data to pie chart
            withdrawEntries.add(PieEntry((resultData.amount * 100 / withdrawSummary).toFloat(), resultData.name))
        }


        //update top up pie chart
        val topUpDataset = PieDataSet(topUpEntries, "Top up")
        topUpDataset.colors = ColorTemplate.MATERIAL_COLORS.toList()
        binding.TopUpPieChart.data = PieData(topUpDataset)
        binding.TopUpPieChart.data.apply {
            setValueFormatter(PercentFormatter())
            setValueTextSize(24f)
            setValueTextColor(Color.WHITE)
        }

        binding.TopUpPieChart.invalidate()

        //update withdraw pie chart
        val withdrawDateset = PieDataSet(withdrawEntries, "Withdraw")
        withdrawDateset.colors = ColorTemplate.MATERIAL_COLORS.toList()
        binding.WithdrawPieChart.data = PieData(withdrawDateset)
        binding.WithdrawPieChart.data.apply {
            setValueFormatter(PercentFormatter())
            setValueTextSize(24f)
            setValueTextColor(Color.WHITE)
        }
        binding.WithdrawPieChart.invalidate()
    }

    private fun updatePreferences(userLogin: String, userPassword: String) {
        PreferenceManager.getDefaultSharedPreferences(this@FlowActivity).edit().apply {
            putString(R.string.UserLogin.toString(), userLogin)
            putString(R.string.UserPassword.toString(), userPassword)
            apply()
        }
    }

}