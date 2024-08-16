layui.use(['table','layer',"form", 'jquery_cookie'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table,
        $ = layui.jquery_cookie($);
        
    // 暂缓列表展示
    var  tableIns = table.render({
        elem: '#customerRepList',
        url : ctx+'/customer_loss/selectReprieve?lossId='+$("input[name='id']").val(),
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "customerRepListTable",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true", width: 95, align: "center"},
            {field: 'measure', title: '暂缓措施',align:"center"},
            {field: 'createDate', title: '创建时间',align:"center"},
            {field: 'updateDate', title: '更新时间',align:"center"},
            {title: '操作',fixed:"right",align:"center", minWidth:150,templet:"#customerRepListBar"}
        ]]
    });


    /**
     * 弹出暂缓添加/编辑页面
     * @param content
     * @param title
     */
    function openAddOrUpdate(content, title) {
        layui.layer.open({
            type: 2,
            content: content
            ,area: ["700px", "205px"]
            ,maxmin: true
            ,title: title
        });
    }


    /**
     * 头部工具栏事件
     */
    table.on('toolbar(customerReps)', function (obj) {
        var checkStatus = table.checkStatus(obj.config.id);
        switch (obj.event) {
            case 'add':

                var content = ctx + '/customer_loss/toRepAddUpdate?lossId=' + $("[name='id']").val();
                var title = "流失管理--添加暂缓";
                openAddOrUpdate(content, title);
                break;

            case 'confirm':

                var id = $("[name='id']").val();
                var cusManager = $.cookie("trueName");

                layer.confirm('确定标记当前客户为流失状态吗？', function (index) {
                    layer.close(index);

                    layer.prompt({title: '请描述流失原因', formType: 2}, function(text, index){
                        layer.close(index);
                        // alert(text);

                        var tips = top.layer.msg("数据正在提交，清扫等...", {icon: 16, time: false, shade: 0.8});

                        $.ajax({
                            type: 'post',
                            url: ctx + '/customer_loss/sureLoss?id=' + id,
                            data: {
                                lossReason: text,
                                cusManager: cusManager
                            },
                            success: function (resp) {
                                if (resp.code == 200) {
                                    top.layer.msg(resp.msg, {icon: 6});
                                    top.layer.close(tips);
                                    layer.closeAll('iframe');
                                    parent.location.reload();
                                } else {
                                    layer.msg(resp.msg, {icon: 5});
                                }
                            }
                        });
                    });
                });

        }
    });


    /**
     * 单元格工具栏事件
     */
    table.on('tool(customerReps)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        var tr = obj.tr;

        if (layEvent === 'edit') {

            var content = ctx + '/customer_loss/toRepAddUpdate?id=' + data.id;
            var title = "流失管理--编辑暂缓";
            openAddOrUpdate(content, title);

        } else if (layEvent === 'del') {

            layer.confirm('确定删除暂缓数据吗？', function (index) {
                obj.del();
                layer.close(index);

                $.ajax({
                    type: 'post',
                    url: ctx + '/customer_loss/delete?id=' + data.id,
                    success: function (resp) {
                        if (resp.code == 200) {
                            layer.msg(resp.msg, {icon: 6});
                        } else {
                            layer.msg(resp.msg, {icon: 5});
                        }
                    }
                });

            });

        }

    });


});
