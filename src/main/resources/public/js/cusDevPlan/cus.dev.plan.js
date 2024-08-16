layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    /**
     * 格式化分配状态方法
     * @param state
     * @returns {string}
     */
    function formatStateResult(state) {
        if (state == 0) {
            return "<div style='color: yellow'>未分配</div>"
        } else if (state == 1) {
            return "<div style='color: green'>已分配</div>"
        } else {
            return "<div style='color: red'>未知</div>"
        }
    }

    function formatDevResult(devResult) {
        if (devResult == 0) {
            return "<div style='color: yellow'>未开发</div>"
        } else if (devResult == 1) {
            return "<div style='color: #00FF00'>开发中</div>"
        } else if (devResult == 2) {
            return "<div style='color: #00B83F'>开发成功</div>"
        } else if (devResult == 3) {
            return "<div style='color: red'>开发失败</div>"
        } else {
            return "<div style='color: #af0000'>未知</div>"
        }
    }

    // 生成数据表格
    var tableIns = table.render({
        elem: '#saleChanceList'
        ,url: ctx + '/cus_dev_plan/list' //数据接口
        // ,toolbar: "#toolbarDemo"
        ,page: true //开启分页
        ,height: "full-125"
        ,limits: [10,15,20,25]
        ,cellMinWidth: 95
        ,id: "saleChanceListTable"
        ,cols: [[ //表头
            {type: "checkbox", fixed: 'center'}
            ,{field: 'id', title: 'ID', sort: true, fixed: 'true', align: "center"}
            ,{field: 'chanceSource', title: '机会来源', align: "center"}
            ,{field: 'customerName', title: '客户名', sort: true, align: "center"}
            ,{field: 'cgjl', title: '成功几率', align: "center"}
            ,{field: 'overview', title: '概要', align: "center"}
            ,{field: 'linkMan', title: '联系人', sort: true, align: "center"}
            ,{field: 'linkPhone', title: '联系电话', sort: true, align: "center"}
            ,{field: 'description', title: '描述', align: "center"}
            ,{field: 'createMan', title: '创建人', sort: true, align: "center"}
            ,{field: 'createDate', title: '创建时间', sort: true, align: "center"}
            ,{field: 'assignMan', title: '指派人', sort: true, align: "center"}
            ,{field: 'assignTime', title: '分配时间', sort: true, align: "center"}
            ,{field: 'state', title: '分配状态', sort: true, align: "center", templet: function (d) {
                    return formatStateResult(d.state);
                }}
            ,{field: 'devResult', title: '开发状态', sort: true, align: "center", templet: function (d) {
                    return formatDevResult(d.devResult);
                }}
            ,{title: '操作', templet: '#op', fixed: "right", align: "center", minWidth: 150}
        ]]
    });

    /**
     * 给搜索按钮绑定事件，实现筛选功能
     */
    $("#sbtn").click(function (data) {
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设
                customerName: $("#customerName").val()
                ,createMan: $("#createMan").val()
                ,devResult: $("#devResult").val()
            }
            ,page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    });


    /**
     * 单元格事件
     */
    table.on('tool(saleChances)', function(obj){ // 注：test 是 table 原始标签的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var tr = obj.tr; //获得当前行 tr 的 DOM 对象（如果有的话）

        if(layEvent === 'dev'){
            //do somehing
            layui.layer.open({
                type: 2,
                content: ctx + '/cus_dev_plan/cus_dev_plan_data?id=' + data.id
                ,area: ["750px", "550px"]
                ,maxmin: true
                ,title: "开发--编辑"
            });

        } else if(layEvent === 'info'){ //删除
            layui.layer.open({
                type: 2,
                content: ctx + '/cus_dev_plan/cus_dev_plan_data?id=' + data.id
                ,area: ["750px", "550px"]
                ,maxmin: true
                ,title: "开发--详情"
            });
        }
    });

});
