package com.loan.icreditapp.bean.loan

class TrialResponseBean {

    //总利息金额
    var totalInterestAmount: String? = null
    //总放款金额
    var totalDisburseAmount: String? = null
    //还款总金额
    var totalRepaymentAmount: String? = null
    //还款日
    var repayDate: String? = null
    //总服务费
    var totalFeeAmount: String? = null
    //分期：第一期，第二期
    var trial: List<Trial>? = null

    class Trial {
        //当期借款金额
        var loanAmount: String? = null

        //当期放款金额
        var amountDisburse: String? = null

        //当期到期应还总金额
        var totalAmount: String? = null

        //放款时间
        var disburseTime: String? = null

        //放款时间
        var disburseTimeMills: String? = null

        //还款日
        var repayDate: String? = null

        //还款日
        var repayDateMills: Long? = null

        //服务费
        var fee: String? = null

        //砍头服务费
        var feePrePaid: String? = null

        //利息
        var interest: Long? = null

        //砍头利息
        var interestPrePaid: String? = null

        //当期Number
        var stageNo: String? = null

    }
}