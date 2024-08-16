layui.use(['table', 'treetable'], function () {
    var $ = layui.jquery;
    var table = layui.table;
    var treeTable = layui.treetable;

    // 渲染表格
    treeTable.render({
        treeColIndex: 1,
        treeSpid: -1,
        treeIdName: 'id',
        treePidName: 'parentId',
        elem: '#munu-table',
        url: ctx+'/module/list2',
        toolbar: "#toolbarDemo",
        treeDefaultClose:true,
        page: true,
        cols: [[
            {type: 'numbers'},
            {field: 'moduleName', width: 300, title: '菜单名称'},
            {field: 'optValue', title: '权限码',width: 80, align: "center"},
            {field: 'url', title: '菜单url'},
            {field: 'createDate', title: '创建时间', width: 200, align: "center"},
            {field: 'updateDate', title: '更新时间', width: 200, align: "center"},
            {
                field: 'grade', width: 80, align: 'center', templet: function (d) {
                    if (d.grade == 0) {
                        return '<span class="layui-badge layui-bg-blue">目录</span>';
                    }
                    if(d.grade==1){
                        return '<span class="layui-badge-rim">菜单</span>';
                    }
                    if (d.grade == 2) {
                        return '<span class="layui-badge layui-bg-gray">按钮</span>';
                    }
                }, title: '类型'
            },
            {templet: '#auth-state', width: 280, align: 'center', title: '操作'}
        ]],
        done: function () {
            layer.closeAll('loading');
        }
    });

    /**
     * 头部工具栏事件
     */
    table.on('toolbar(munu-table)', function(obj){
        var checkStatus = table.checkStatus(obj.config.id);
        switch(obj.event){
            case 'expand':
                // layer.msg('添加');
                treeTable.expandAll('#munu-table');
                break;
            case 'fold':
                // layer.msg('删除');
                treeTable.foldAll('#munu-table');
                break;
            case 'add':
                layer.open({
                    type: 2,
                    title: '<h3>菜单管理--添加</h3>',
                    area: ['656px', '390px'],
                    content: ctx + '/module/toAddUpdate?grade=0&parentId=-1'
                });
        };
    });


    /**
     * 单元格工具事件
     */
    table.on('tool(munu-table)', function(obj){ // 注：test 是 table 原始标签的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var tr = obj.tr; //获得当前行 tr 的 DOM 对象（如果有的话）

        var pId = data.id;
        var gId = data.grade+1;

        if(layEvent === 'add'){ //查看
            //do somehing
            console.log(data);

            // 判断是否是4级菜单
            if (gId == 3) {
                layer.msg("暂不支持添加四级菜单！", {icon: 5});
                return;
            }

            layer.open({
                type: 2,
                title: '<h3>菜单管理--添加</h3>',
                area: ['656px', '435px'],
                content: ctx + '/module/toAddUpdate?grade=' + gId + '&parentId=' + pId
            });

        } else if(layEvent === 'del'){ //删除
            layer.confirm('确定删除吗？', function(index){
                obj.del(); // 删除对应行（tr）的 DOM 结构，并更新缓存
                layer.close(index);

                $.ajax({
                    type: 'POST',
                    url: ctx + '/module/delete?id=' + pId,
                    success: function (resp) {
                        console.log(resp);
                        if (resp.code == 200) {
                            layer.msg("数据删除成功！", {icon: 6});
                        } else {
                            layer.msg(resp.msg, {icon: 5});
                        }
                    }
                });

            });
        } else if(layEvent === 'edit'){ //编辑
            //do something

            var id = data.id;
            layer.open({
                type: 2,
                title: '<h3>菜单管理--修改</h3>',
                area: ['656px', '435px'],
                content: ctx + '/module/updateModule?mid=' + id
            });
        }
    });

    

    
});