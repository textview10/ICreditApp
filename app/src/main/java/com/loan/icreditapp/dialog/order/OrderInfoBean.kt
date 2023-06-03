package com.loan.icreditapp.dialog.order

class OrderInfoBean {
    //订单ID
    var orderId: String? = null

    //被拒绝原因
    var reason: String? = null

    //订单状态	"1":approving "2":active "3":paid "4":overdue "5":"declined" "6":"repaying"
    var status: String? = null

    //token
    var token: String? = null

    //是否可申请借款	true or false
    var canApply: Boolean? = false

    //被限制申请天数
    var limitday: Int? = 0

    //总应还金额
    var totalAmount: Double? = 0e10

    //账期
    var stageList: List<Stage>? = null
    //1是首次, 0是非首次
    var firstApprove : Int? = null
    //0 首贷  大于0复贷
    var reloan : Int? = null

    fun isFirstLoan() : Boolean{
        if (reloan == 0){
            return true
        }
        return false
    }

    class Stage {
        //当期期号
        var stageNo: String? = null

        //当期状态 "1":approving "2":active "3":paid "4":overdue "5":"declined","6":"repaying"
        var status: String? = null

        //当期应还总金额
        var totalAmount: Double? = 0e10

        //放款时间
        var disburseTime: String? = null

        //应还日期
        var repayDate: String? = null

        //应还本金
        var amount: Double? = null

        //应还利息
        var interest: Double? = null

        //砍头利息
        var interestPrePaid: Double? = null

        //服务费
        var fee: Double? = null

        //砍头服务费
        var feePrePaid: Double? = null

        //罚息
        var penalty: Double? = null

        //是否可以还款
        var payable: Boolean? = false

    }

}