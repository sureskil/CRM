layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;


    form.on('submit(addOrUpdateSaleChance)', function(data){
        // data中是提交表单中的相关信息
        // console.log(data);

        var sid = $("#sid").val();
        var urlt;
        if (sid == null || sid == '') {
            urlt = ctx + '/sale_chance/add';
        } else {
            urlt = ctx + '/sale_chance/updateSaleChance?id='+ sid;
        }

        var tips = top.layer.msg("数据提交中，请稍等...", {
            icon: 16,
            // 不自动关闭
            time: false,
            // 遮罩的透明度
            shade: 0.8
        });

        $.ajax({
            type: 'POST',
            // url: ctx + '/sale_chance/add',
            url: urlt,
            data: data.field,
            success: function (resp) {
                console.log(resp);
                if (resp.code == 200 && (sid == null || sid == '')) {
                    layer.close(tips);
                    // layer.closeAll('iframe');
                    layer.msg("数据添加成功！", {time: 1300, icon:6});
                    setTimeout("window.parent.location.reload()", 1300);
                } else if (resp.code == 200) {
                    layer.close(tips);
                    layer.msg("数据修改成功！", {time: 1300, icon:6});
                    setTimeout("window.parent.location.reload()", 1300);
                } else{
                    layer.msg(resp.msg, {time: 1300, icon: 5});
                }
            }
        });


        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });

    // 通过取消按钮关闭弹出框
    $("#closeBtn").click(function () {
        // 先得到当前iframe层的索引
        var index = parent.layer.getFrameIndex(window.name);
        // 执行关闭
        parent.layer.close(index);
    });

    // 给指派人下拉框添加选择内容
    var sels = $("#assignMan");
    var val = $("#assignManId").val();
    $.ajax({
        type: 'POST',
        url: ctx + '/sale_chance/queryAllSaleUser',
        // data: data,
        success: function (resp) {
            // console.log(resp);

            if (resp != null && resp.length > 0) {

                for (let i = 0; i < resp.length; i++) {
                    var opt = "";

                    if (val == resp[i].id) {
                        opt = "<option selected value='"+resp[i].id+"'>"+resp[i].true_name+"</option>"
                    } else {
                        opt = "<option value='"+resp[i].id+"'>"+resp[i].true_name+"</option>"
                    }
                    sels.append(opt);
                }

            }

            layui.form.render("select");
        }
    });

});