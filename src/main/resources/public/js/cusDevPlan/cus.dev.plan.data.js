layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    var sid = $("#saleChanceId").val();

    /**
     * 生成数据表格
     */
    var tableIns = table.render({
        elem: '#cusDevPlanList'
        ,url: ctx + '/cus_dev_plan/queryCusDevPlan?saleChanceId=' + sid //数据接口
        ,toolbar: "#toolbarDemo"
        ,page: true //开启分页
        ,height: "full-125"
        ,limits: [10,15,20,25]
        ,cellMinWidth: 95
        ,id: "saleChanceListTable"
        ,cols: [[ //表头
            {type: "checkbox", fixed: 'center'}
            ,{field: 'id', title: '编号', sort: true, fixed: 'true', align: "center"}
            ,{field: 'planItem', title: '计划项', align: "center"}
            ,{field: 'exeAffect', title: '执行效果', sort: true, align: "center"}
            ,{field: 'planDate', title: '执行时间', align: "center"}
            ,{field: 'createDate', title: '创建时间', align: "center"}
            ,{field: 'updateDate', title: '更新时间', sort: true, align: "center"}
            ,{title: '操作', templet: '#cusDevPlanListBar', fixed: "right", align: "center", minWidth: 150}
        ]]
    });


    /**
     * 头部工具栏事件
     */
    table.on('toolbar(cusDevPlans)', function(obj){
        var checkStatus = table.checkStatus(obj.config.id);

        var flag;
        switch(obj.event){
            case 'add':
                // layer.msg('添加');

                openAddOrUpdate();
                break;
            case 'success':
                // layer.msg('删除');

                flag = 1;
                layer.confirm('确认执行当前操作？', {icon: 3, title:'提示'}, function(index){
                    //do something
                    $.ajax({
                        type: 'POST',
                        url: ctx + '/cus_dev_plan/devResult?flag=' + flag,
                        data: {id: sid},
                        success: function (resp) {
                            // console.log(resp);
                            if (resp.code == 200) {
                                layer.msg("数据更新成功！", {time: 1300, icon: 6}, function () {
                                    window.parent.location.reload();
                                })
                            } else {
                                layer.msg("数据更新失败！", {icon: 5})
                            }
                        },
                        error: function () {}
                    });

                    layer.close(index);
                });
                break;

            case 'failed':
                layer.msg('编辑');

                flag = 2;
                layer.confirm('确认执行当前操作吗？', {icon: 3, title:'提示'}, function(index){
                    //do something
                    $.ajax({
                        type: 'POST',
                        url: ctx + '/cus_dev_plan/devResult?flag=' + flag,
                        data: {id: sid},
                        success: function (resp) {
                            // console.log(resp);
                            if (resp.code == 200) {
                                layer.msg("数据更新成功！", {time: 1300, icon: 6}, function () {
                                    window.parent.location.reload();
                                })
                            } else {
                                layer.msg("数据更新失败！", {icon: 5})
                            }
                        },
                        error: function () {}
                    });

                    layer.close(index);
                });
        };
    });

    /**
     * 单元格工具事件
     */
    table.on('tool(cusDevPlans)', function(obj){ // 注：test 是 table 原始标签的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        console.log(data);
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var tr = obj.tr; //获得当前行 tr 的 DOM 对象（如果有的话）

        var cid = data.id;
        if(layEvent === 'edit'){ //查看
            //do somehing
            openAddOrUpdate(cid);

        } else if(layEvent === 'del'){ //删除
            layer.confirm('确定删除吗？', function(index){
                obj.del(); // 删除对应行（tr）的 DOM 结构，并更新缓存

                $.ajax({
                    type: 'POST',
                    url: ctx + '/cus_dev_plan/deleteCusDevPlan',
                    data: {id: cid},
                    success: function (resp) {
                        // console.log(resp);
                        if (resp.code == 200) {
                            layer.msg("数据删除成功！", {icon: 6});
                            tableIns.reload();
                        } else {
                            layer.msg(resp.msg, {icon: 5});
                        }
                    }
                });

                layer.close(index);
            });
        }
    });

    /**
     * 打开添加或编辑页面
     */
    function openAddOrUpdate(cid) {

        var title1,
            url;

        if (cid == null) {
            title1 = "计划项管理--添加计划项";
            url1 = ctx + '/cus_dev_plan/add_update?sid=' + sid;
        } else {
            title1 = "计划项管理--编辑计划项";
            url1 = ctx + '/cus_dev_plan/add_update?sid=' + sid +'&cid=' + cid;
        }

        layer.open({
            title: title1,
            type: 2,
            area: ["541px", "306px"],
            content: url1
        });
    }

});
