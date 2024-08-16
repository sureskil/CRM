layui.use(['form', 'layer', 'formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
    var formSelects = layui.formSelects;


    var sid = $("[name=id]").val();

    form.on('submit(addOrUpdateUser)', function(data){
        console.log(data.elem) //被执行事件的元素DOM对象，一般为button对象
        console.log(data.form) //被执行提交的form对象，一般在存在form标签时才会返回
        console.log(data.field) //当前容器的全部表单字段，名值对形式：{name: value}

        var tips = top.layer.msg("数据提交中，请稍等...", {
            icon: 16,
            // 不自动关闭
            time: false,
            // 遮罩的透明度
            shade: 0.8
        });

        let u1;
        if (sid) {
            u1 = ctx + '/user/update?id=' + sid;
        } else {
            u1 = ctx + '/user/add';
        }

        $.ajax({
            type: 'POST',
            url: u1,
            data: data.field,
            success: function (resp) {
                if (resp.code == 200 && sid) {
                    // console.log(resp);
                    layer.close(tips);
                    // layer.closeAll('iframe');
                    layer.msg("数据修改成功！", {time: 1300, icon:6});
                    setTimeout("window.parent.location.reload()", 1300);
                } else if (resp.code == 200) {
                    // console.log(resp);
                    layer.close(tips);
                    // layer.closeAll('iframe');
                    layer.msg("数据添加成功！", {time: 1300, icon:6});
                    setTimeout("window.parent.location.reload()", 1300);
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


    /**
     * 加载下拉框
     */
    formSelects.config('selectId', {
        type: 'post',                //请求方式: post, get, put, delete...
        searchUrl: ctx + '/role/roleIdAndName?id=' + sid,              //搜索地址, 默认使用xm-select-search的值, 此参数优先级高
        keyName: 'roleName',            //自定义返回数据中name的key, 默认 name
        keyVal: 'id',            //自定义返回数据中value的key, 默认 value
        direction: 'auto',          //多选下拉方向, auto|up|down
    }, true);

});