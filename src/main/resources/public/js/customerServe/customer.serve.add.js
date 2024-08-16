layui.use(['form', 'layer','jquery_cookie'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);


    form.on('submit(addCustomerServe)', function (data) {
        // console.log(data.elem);
        // console.log(data.form);
        // console.log(data.field);

        var createPeople = $.cookie("trueName");

        var tips = layer.msg("数据提交中，请稍等...", {icon: 16, time: false, shade: 0.8});

        $.ajax({
            type: 'post',
            url: ctx + "/customer_serve/add?createPeople=" + createPeople,
            data: data.field,
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

        return false;
    });


    // 通过取消按钮关闭弹出框
    $("#closeBtn").click(function () {
        // 先得到当前iframe层的索引
        var index = parent.layer.getFrameIndex(window.name);
        // 执行关闭
        parent.layer.close(index);
    });

});