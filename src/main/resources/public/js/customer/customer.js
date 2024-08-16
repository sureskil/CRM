layui.use(['table','layer',"form"],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table,
        form = layui.form;

    //客户列表展示
    var  tableIns = table.render({
        elem: '#customerList',
        url : ctx+'/customer/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "customerListTable",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true",align: "center"},
            {field: 'name', title: '客户名',align:"center"},
            {field: 'fr', title: '法人',  align:'center'},
            {field: 'khno', title: '客户编号', align:'center'},
            {field: 'area', title: '地区', align:'center'},
            {field: 'cusManager', title: '客户经理',  align:'center'},
            {field: 'myd', title: '满意度', align:'center'},
            {field: 'level', title: '客户级别', align:'center'},
            {field: 'xyd', title: '信用度', align:'center'},
            {field: 'address', title: '详细地址', align:'center'},
            {field: 'postCode', title: '邮编', align:'center'},
            {field: 'phone', title: '电话', align:'center'},
            {field: 'webSite', title: '网站', align:'center'},
            {field: 'fax', title: '传真', align:'center'},
            {field: 'zczj', title: '注册资金', align:'center'},
            {field: 'yyzzzch', title: '营业执照', align:'center'},
            {field: 'khyh', title: '开户行', align:'center'},
            {field: 'khzh', title: '开户账号', align:'center'},
            {field: 'gsdjh', title: '国税', align:'center'},
            {field: 'dsdjh', title: '地税', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center'},
            {field: 'updateDate', title: '更新时间', align:'center'},
            {title: '操作', templet:'#customerListBar',fixed:"right",align:"center", minWidth:150}
        ]]
    });


    /**
     * 给搜索按钮绑定事件，实现筛选功能
     */
    $("#sbtn").click(function (data) {
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设
                name: $("#customerName").val()
                ,khno: $("#khno").val()
                ,level: $("#level").val()
            }
            ,page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    });


    /**
     * 弹出新增或更新页面
     */
    function openAddOrUpdate(content) {
        layui.layer.open({
            type: 2,
            content: content
            ,area: ["750px", "612px"]
            ,maxmin: true
            ,title: "客户管理--添加客户"
        });
    }


    /**
     * 头部工具栏事件
     */
    table.on('toolbar(customers)', function(obj){
        var checkStatus = table.checkStatus(obj.config.id);
        switch(obj.event){
            case 'add':
                // layer.msg('添加');
                var content = ctx + '/customer/toAddUpdate';
                openAddOrUpdate(content);
                break;

            case 'order':

                // console.log(checkStatus);

                if (checkStatus.data.length == 0) {
                    layer.msg("没有数据被选中！", {icon: 5});
                    return;
                } else if(checkStatus.data.length > 1) {
                    layer.msg("不支持多选", {icon: 5});
                    return;
                }

                layui.layer.open({
                    type: 2,
                    content: ctx + '/customer/toOrder?id=' + checkStatus.data[0].id
                    ,area: ["1357px", "612px"]
                    ,maxmin: true
                    ,title: "客户管理--订单查看"
                });

        };
    });



    //单元格工具事件
    table.on('tool(customers)', function(obj){ // 注：test 是 table 原始标签的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var tr = obj.tr; //获得当前行 tr 的 DOM 对象（如果有的话）

        if(layEvent === 'del'){ //删除
            layer.confirm('确定删除吗？', function(index){
                obj.del(); // 删除对应行（tr）的 DOM 结构，并更新缓存
                layer.close(index);

                $.ajax({
                    type: 'post',
                    url: ctx + '/customer/delete?id=' + data.id,
                    success: function (resp) {
                        if (resp.code == 200) {
                            layer.msg(resp.msg, {icon: 6});
                        }
                    }
                });

            });
        } else if(layEvent === 'edit'){ //编辑
            //do something
            var content = ctx + '/customer/toAddUpdate?id=' + data.id;
            openAddOrUpdate(content);
        }
    });


});
