layui.use(['table','layer'],function(){
       var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;


       /**
        * 生成数据表格
        */
       var tableIns = table.render({
              elem: '#roleList'
              ,url: ctx + '/role/list' //数据接口
              ,toolbar: "#toolbarDemo"
              ,page: true //开启分页
              ,height: "full-125"
              ,limits: [10,15,20,25]
              ,cellMinWidth: 95
              ,id: "roleListTable"
              ,cols: [[ //表头
                     {type: "checkbox", fixed: 'center'}
                     ,{field: 'id', title: '编号', sort: true, fixed: 'true', align: "center"}
                     ,{field: 'roleName', title: '角色名', align: "center"}
                     ,{field: 'roleRemark', title: '角色备注', sort: true, align: "center"}
                     ,{field: 'updateDate', title: '更新时间', align: "center"}
                     ,{title: '操作', templet: '#roleListBar', fixed: "right", align: "center", minWidth: 150}
              ]]
       });


       /**
        * 给多条搜索按钮绑定事件
        */
       $(".search_btn").click(function () {
              tableIns.reload({
                     where: { //设定异步数据接口的额外参数，任意设
                            roleName: $("[name='roleName']").val()
                     }
                     ,page: {
                            curr: 1 //重新从第 1 页开始
                     }
              });
       });

       /**
        * 代开添加/更新页面
        */
       function addorUpdate(sid) {

              let title1;
              let content1;

              if (sid) {
                     title1 = '<h3>角色管理--编辑</h3>'
                     content1 = ctx + '/role/addUpdate?id=' + sid
              } else {
                     title1 = '<h3>角色管理--添加</h3>'
                     content1 = ctx + '/role/addUpdate'
              }

              layer.open({
                     type: 2,
                     title: title1,
                     area: ['444px', '238px'],
                     content: content1
              });
       }

       /**
        * 监听头部工具栏
        */
       table.on('toolbar(roles)', function(obj){
              var checkStatus = table.checkStatus(obj.config.id);
              // console.log(checkStatus);
              // console.log("length: " + checkStatus.data.length);

              if (checkStatus.data.length < 1) {
                     layer.msg("没有角色被选中！", {icon: 5});
                     return;
              }

              // 如果要批量授权的话，就把这个判断去掉！
              if (checkStatus.data.length > 1) {
                     layer.msg("暂不支持批量授权！", {icon: 5});
                     return;
              }

              var rid = checkStatus.data[0].id;
              console.log(rid);

              switch(obj.event){
                     case 'add':
                            // layer.msg('添加');
                            addorUpdate();

                            break;
                     case 'grant':
                            // layer.msg('删除');

                            layer.open({
                                   type: 2,
                                   title: '<h3>角色管理--授权</h3>',
                                   // area: ['568px', '710px'],
                                   area: ['397px', '559px'],
                                   content: ctx + '/role/toGrant?id=' + rid
                            });

              };
       });


       /**
        * 单元格工具事件
        */
       table.on('tool(roles)', function(obj){ // 注：test 是 table 原始标签的属性 lay-filter="对应的值"
              var data = obj.data; //获得当前行数据
              var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
              var tr = obj.tr; //获得当前行 tr 的 DOM 对象（如果有的话）

              if(layEvent === 'del'){ //删除
                     layer.confirm('确定删除吗？', function(index){
                            obj.del(); // 删除对应行（tr）的 DOM 结构，并更新缓存
                            layer.close(index);

                            $.ajax({
                                   type: 'POST',
                                   url: ctx + '/role/delete',
                                   data: {id: data.id},
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
                     addorUpdate(data.id);
              }
       });

});
