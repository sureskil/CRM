layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    form.on('submit(addOrUpdateCustomerRep)', function (data) {
        // console.log(data.elem);
        // console.log(data.form);
        // console.log(data.field);

        var id = $("[name = 'id']").val();
        let url;
        if (id) {
            url = ctx + '/customer_loss/update?id=' + $("[name='id']").val()
        } else {
            url = ctx + '/customer_loss/add?lossId=' + $("[name='lossId']").val();
        }

        var tips = top.layer.msg("数据提交中，请骚等...", {icon: 16, time: false, shade: 0.8});

        $.ajax({
            type: 'post',
            url: url,
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


    /**
     * 按钮关闭事件
     */
    $("#closeBtn").click(function () {
        // 先得到当前iframe层的索引
        var index = parent.layer.getFrameIndex(window.name);
        // 执行关闭
        parent.layer.close(index);
    });
});