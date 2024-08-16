layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    var sid = $("#sid").val();
    console.log(sid);

    form.on('submit(addOrUpdateCusDevPlan)', function(data){
        console.log(data.field) //当前容器的全部表单字段，名值对形式：{name: value}

        var tips = top.layer.msg("数据提交中，请稍等...", {
            icon: 16,
            // 不自动关闭
            time: false,
            // 遮罩的透明度
            shade: 0.8
        });

        var cid = $("#cid").val(),
            url2;

        if (cid) {
            url2 = ctx + '/cus_dev_plan/updateCusDevPlan?sid=' + sid + '&id=' + cid;
        } else {
            url2 = ctx + '/cus_dev_plan/add?sid=' + sid;
        }

        $.ajax({
            type: 'POST',
            // url: ctx + '/cus_dev_plan/add?sid=' + sid,
            url: url2,
            data: data.field,
            success: function (resp) {
                if (resp.code == 200 && cid) {
                    // layer.close(tips);
                    top.layer.close(tips);
                    layer.msg("数据编辑成功！", {time: 1300, icon:6}, function () {
                        window.parent.location.reload();
                    });
                } else if (resp.code == 200){
                    top.layer.close(tips);
                    layer.msg("数据添加成功！", {time: 1300, icon:6}, function () {
                        window.parent.location.reload();
                    });
                } else {
                    layer.msg(resp.msg, {time: 1300, icon: 5});
                }
            }
        });

        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });

    $("#closeBtn").click(function () {
        // 先得到当前iframe层的索引
        var index = parent.layer.getFrameIndex(window.name);
        // 执行关闭
        parent.layer.close(index);
    });

});