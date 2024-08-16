layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;


    /**
     * 表单提交事件
     */
    form.on('submit(addOrUpdateCustomerServe)', function (data) {

        var tips = top.layer.msg("数据提交中，请稍等...", {icon: 16, time: false, shade: 0.8});

        $.ajax({
            type: 'post',
            url: ctx + '/customer_serve/updateCustomerServe',
            data: {
                id: $("[name='id']").val(),
                assigner: $("#assigner").val(),
                state: data.field.state
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

        return false;
    });




    /**
     * 拼接下拉框选项
     */
    $.ajax({
        type: 'post',
        url: ctx + '/customer_serve/selectAllAccountManager',
        success: function (resp) {
            // console.log(resp);

            if (resp != null && resp.length > 0) {
                let b = "";
                var a = $("#assigner");

                for (let i = 0; i < resp.length; i++) {

                    if ($("[name=assigner1]").val() == resp[i].id) {
                        b += "<option selected value='" + resp[i].id + "'>" + resp[i].user_name + "</option>"
                    } else {
                        b += "<option value='" + resp[i].id + "'>" + resp[i].user_name + "</option>"
                    }

                }
                a.append(b);
            }

            layui.form.render("select");

        }
    });

    // 通过取消按钮关闭弹出框
    $("#closeBtn").click(function () {
        // 先得到当前iframe层的索引
        var index = parent.layer.getFrameIndex(window.name);
        // 执行关闭
        parent.layer.close(index);
    });

});