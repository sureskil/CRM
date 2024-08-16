layui.use(['table','layer',"form"],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    //客户流失列表展示
    var  tableIns = table.render({
        elem: '#customerLossList',
        url : ctx+'/customer_loss/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "customerLossListTable",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true", align: "center"},
            {field: 'cusNo', title: '客户编号',align:"center"},
            {field: 'cusName', title: '客户名称',align:"center"},
            {field: 'cusManager', title: '客户经理',align:"center"},
            {field: 'lastOrderTime', title: '最后下单时间',align:"center"},
            {field: 'lossReason', title: '流失原因',align:"center"},
            {field: 'confirmLossTime', title: '确认流失时间',align:"center"},
            {field: 'createDate', title: '创建时间',align:"center"},
            {field: 'updateDate', title: '更新时间',align:"center"},
            {title: '操作',fixed:"right",align:"center", minWidth:150,templet:"#op"}
        ]]
    });


    /**
     * 给搜索按钮绑定事件，实现筛选功能
     */
    $("#sbtn").click(function (data) {
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设
                cusNo: $("#cusNo").val()
                ,cusName: $("#cusName").val()
                ,state: $("#state").val()
            }
            ,page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    });

    function openInfoOrAdd(title, id) {
        layui.layer.open({
            type: 2,
            content: ctx + '/customer_loss/toRep?id=' + id
            ,area: ["1075px", "612px"]
            ,maxmin: true
            ,title: title
        });
    }

    /**
     * 单元格工具栏事件
     */
    table.on('tool(customerLosses)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        var tr = obj.tr;

        let title;
        let id;
        if (layEvent === 'info') {

            title = "流失管理--暂缓数据查看";
            id = data.id;
            openInfoOrAdd(title, id);

        } else if (layEvent === 'add') {

            title = "流失管理--暂缓措施维护";
            id = data.id;
            openInfoOrAdd(title, id);

        }

    });



});
