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
        ,url: ctx + '/sale_chance/list' //数据接口
        ,toolbar: "#toolbarDemo"
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
            ,{title: '操作', templet: '#saleChanceListBar', fixed: "right", align: "center", minWidth: 150}
        ]]
    });

    // 给多条加搜索按钮绑定点击事件
    // 与监视事件不同的是：这里没有obj对象，拿不到想要的信息，所以下面用 选择器.val()进行取值
    $(".search_btn").click(function () {
        tableIns.reload( {
            where: { //设定异步数据接口的额外参数，任意设
                customerName: $("#customerName").val()
                ,createMan: $("#createMan").val()
                ,state: $("#state").val()
            }
            ,page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    });

    /**
     * 给数据表格绑定头部工具栏触发事件，事件绑定的都是lay-filter参数
     */
    table.on('toolbar(saleChances)', function(obj){
        // checkStatus中有被选中的id的数据集合（这个集合本质是一个Array容器）
        var checkStatus = table.checkStatus(obj.config.id);
        // console.log(checkStatus);
        switch(obj.event){
            case 'add':
                // layer.msg('添加');
                layer.open({
                    type: 2,
                    title: '<h3>营销机会管理--添加</h3>',
                    area: ['444px', '620px'],
                    content: ctx+'/sale_chance/addOrUpdate' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
                });

                var saleUsers = $("#saleUsers").val();
                console.log(saleUsers);

                break;

            case 'del':
                // layer.msg('删除');
                deleteBatch(checkStatus);
        };
    });

    // 批量删除的方法
    function deleteBatch(data) {
        // 1.先进行非空判断
        let length = data.data.length;
        if (length < 1) {
            layer.msg("请选择要删除的数据！", {icon: 5})
            return;
        }

        // 2.凭借ids字符串，用于在访问后端删除方法时添加ids数组参数
        var ids = "ids=";
        for (let i = 0; i < length; i++) {
            var saleChance = data.data[i];
            if (i == length-1) {
                ids += saleChance.id;
            } else {
                ids += saleChance.id +"&ids=";
            }
        }
        // console.log(ids);

        // 3.弹出一个询问框
        layer.confirm('是否删除被选中的营销数据？', function(index){

            // 4.如果选择是的化，向后端发送ajax请求
            $.ajax({
                type: 'POST',
                url: ctx + '/sale_chance/deleteBatch',
                data: ids,
                success: function (resp) {
                    // console.log(resp);
                    // 如果code=200，证明删除成功，显示对应的提示框，并刷新表格
                    if (resp.code == 200) {
                        layer.msg("营销数据删除成功！", {time: 1300, icon: 6});
                        tableIns.reload({
                            page: {
                                curr: 1 //重新从第 1 页开始
                            }
                        });
                    } else {
                        layui.msg("营销数据删除失败！", {time: 1300, icon: 5});
                    }
                }
            });

            layer.close(index);
        });


    }

    // 单元格工具栏触发事件
    table.on('tool(saleChances)', function(obj){ // 注：test 是 table 原始标签的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var tr = obj.tr; //获得当前行 tr 的 DOM 对象（如果有的话）

        if(layEvent === 'edit'){ //查看
            // console.log(data);
            console.log(data.id);

            layer.open({
                type: 2,
                title: '<h3>营销机会管理--编辑</h3>',
                area: ['444px', '620px'],
                content: ctx+'/sale_chance/addOrUpdate?id='+data.id //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            });



        } else if(layEvent === 'del'){ //删除
            layer.confirm('确定删除这条数据营销数据吗？', function(index){
                obj.del(); // 删除对应行（tr）的 DOM 结构，并更新缓存
                layer.close(index);

                $.ajax({
                    type: 'POST',
                    url: ctx + '/sale_chance/deleteSaleChance',
                    data: {id: data.id},
                    success: function (resp) {
                        // console.log(resp);
                        if (resp.code == 200) {
                            layer.msg("营销机会数据删除成功！", {time: 1300, icon:6});
                        } else {
                            layer.msg(resp.msg, {time: 1300, icon:6});
                        }
                    }
                });
            });
        }
    });


















});
