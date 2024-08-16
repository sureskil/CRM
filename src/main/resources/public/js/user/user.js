layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    /**
     * 生成数据表格
     */
    var tableIns = table.render({
        elem: '#userList'
        ,url: ctx + '/user/list' //数据接口
        ,toolbar: "#toolbarDemo"
        ,page: true //开启分页
        ,height: "full-125"
        ,limits: [10,15,20,25]
        ,cellMinWidth: 95
        ,id: "userListTable"
        ,cols: [[ //表头
            {type: "checkbox", fixed: 'center'}
            ,{field: 'id', title: '编号', sort: true, fixed: 'true', align: "center"}
            ,{field: 'userName', title: '用户名', align: "center"}
            ,{field: 'email', title: '用户邮箱', sort: true, align: "center"}
            ,{field: 'trueName', title: '真实姓名', align: "center"}
            ,{field: 'phone', title: '手机号码', align: "center"}
            ,{field: 'createDate', title: '创建时间', align: "center"}
            ,{field: 'updateDate', title: '更新时间', sort: true, align: "center"}
            ,{title: '操作', templet: '#userListBar', fixed: "right", align: "center", minWidth: 150}
        ]]
    });

    /**
     * 多条件筛选查询
     */
    $(".search_btn").click(function () {
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设
                /*userName: $("input[name=userName]").val()
                ,email: $("input[name=email]").val()
                ,phone: $("input[name=phone]").val()*/
                userName: $("#userName").val()
                ,email: $("#email").val()
                ,phone: $("#phone").val()
            }
            ,page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    });

    /**
     * 打开添加、编辑页面
     */
    function addOrUpdate(sid) {

        let t1;
        let c1;

        if (sid == null) {
            t1 = "用户管理--添加用户";
            c1 = ctx + '/user/addUpdateUser';
        } else {
            t1 = "用户管理--用户更新";
            c1 = ctx + '/user/addUpdateUser?id=' + sid
        }

        layer.open({
            type: 2,
            title: t1,
            area: ["547px", "382px"],
            content: c1
        });
    }

    /**
     * 头部工具栏事件
     */
    table.on('toolbar(users)', function(obj){
        var checkStatus = table.checkStatus(obj.config.id);
        switch(obj.event){
            case 'add':
                // layer.msg('添加');

                addOrUpdate();

                break;
            case 'del':
                // layer.msg('删除');
                // console.log(checkStatus.data);

                if (checkStatus.data.length < 1) {
                    layer.msg("没有数据被选中！", {icon: 5});
                    return;
                }

                let ids="ids=";
                for (let i = 0; i < checkStatus.data.length; i++) {

                    if (i == checkStatus.data.length-1) {
                        ids += checkStatus.data[i].id;
                    } else {
                        ids += checkStatus.data[i].id + "&ids=";
                    }
                }
                // console.log(ids);

                layer.confirm('确定删除所选择的用户数据吗？', {icon: 3, title:'提示'}, function(index){
                    //do something

                    $.ajax({
                        type: 'POST',
                        url: ctx + '/user/deleteBatch?' + ids,
                        success: function (resp) {
                            // console.log(resp);
                            if (resp.code == 200) {
                                tableIns.reload({
                                    done: function (resp, curr) {
                                        if (curr > 1) {
                                            curr = curr - 1;
                                            tableIns.reload({
                                                page: {
                                                    curr: curr
                                                },
                                            })
                                        }
                                    }
                                });
                            } else {
                                layer.msg(resp.msg, {icon: 5});
                            }
                        }
                    });

                    layer.close(index);
                });
        };
    });

    /**
     * 单元格工具事件
     */
    table.on('tool(users)', function(obj){ // 注：test 是 table 原始标签的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        console.log(data);
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var tr = obj.tr; //获得当前行 tr 的 DOM 对象（如果有的话）

        if(layEvent === 'edit'){ //查看
            //do somehing
            addOrUpdate(data.id);

        } else if(layEvent === 'del'){ //删除

            var sid1 = data.id;

            layer.confirm('确定删除吗？', function(index){

                $.ajax({
                    type: 'POST',
                    url: ctx + '/user/deleteUser',
                    data: {id: sid1},
                    success: function (resp) {
                        // console.log(resp);
                        if (resp.code == 200) {
                            layer.msg("数据删除成功！" ,{icon: 6});
                        } else {
                            layer.msg(resp.msg, {icon: 5});
                        }
                    }
                });

                obj.del(); // 删除对应行（tr）的 DOM 结构，并更新缓存
                layer.close(index);
            });
        } else if(layEvent === 'edit'){ //编辑
            //do something

            // 同步更新缓存对应的值
            // 该方法仅为前端层面的临时更新，在实际业务中需提交后端请求完成真实的数据更新。
            obj.update({
                username: '123',
                title: 'abc'
            });
            // 若需更新其他包含自定义模板并可能存在关联的列视图，可在第二个参数传入 true
            obj.update({
                username: '123'
            }, true); // 注：参数二传入 true 功能为 v2.7.4 新增

            // 当发送后端请求成功后，可再通过 reloadData 方法完成数据重载
            /*
            table.reloadData(id, {
              scrollPos: 'fixed'  // 保持滚动条位置不变 - v2.7.3 新增
            });
            */
        }
    });

});