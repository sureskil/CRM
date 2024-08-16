layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;





    /**
     * 表单提交事件
     */
    form.on('submit(addOrUpdateCustomer)', function(data){
        // console.log(data.elem) //被执行事件的元素DOM对象，一般为button对象
        // console.log(data.form) //被执行提交的form对象，一般在存在form标签时才会返回
        // console.log(data.field) //当前容器的全部表单字段，名值对形式：{name: value}

        let url;
        let id = $("[name='id']").val();
        if (id) {
            url = ctx + '/customer/update?id=' + id;
        } else {
            url = ctx + '/customer/add';
        }

        console.log(id);
        // return ;

        var tips = top.layer.msg("数据提交中，请稍等...", {
            icon: 16,
            // 不自动关闭
            time: false,
            // 遮罩的透明度
            shade: 0.8
        });

        $.ajax({
            type: 'POST',
            url: url,
            data: data.field,
            success: function (resp) {
                if (resp.code == 200){
                    top.layer.msg("操做成功！", {icon: 6})
                    top.layer.close(tips);
                    layer.closeAll("iframe");
                    parent.location.reload();
                } else {
                    layer.msg(resp.msg, {icon: 5});
                }
            }
        });


        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });



    /**
     * 通过取消按钮关闭弹出框
     */
    $("#closeBtn").click(function () {
        // 先得到当前iframe层的索引
        var index = parent.layer.getFrameIndex(window.name);
        // 执行关闭
        parent.layer.close(index);
    });

});