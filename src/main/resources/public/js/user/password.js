layui.use(['form', 'jquery', 'jquery_cookie'], function(){
    var form = layui.form,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    /**
     * 用户修改密码功能
     */
    form.on('submit(saveBtn)', function(data){

        var oldPwd = data.field.old_password,
            newPwd = data.field.new_password,
            rePwd = data.field.again_password;

        // TODO 判断参数是否为空

        if (oldPwd == newPwd) {
            layer.msg("新旧密码不能相同！", {icon: 5});
            return ;
        } else if (!newPwd == rePwd) {
            layer.msg("两次输入的新密码不一致！", {icon: 5});
            return ;
        }

        $.ajax({
            type: 'POST',
            url: ctx+'/user/uup',
            data: {
                // 因为上面的密码初始判断中对这三个参数起了变量名，所以这里直接写就可以，但这么写也不错
                oldPwd: data.field.old_password,
                newPwd: data.field.new_password,
                rePwd: data.field.again_password
            },
            success: function (resp) {
                console.log(resp);
                if (resp.code == 200) {
                    layer.msg("密码修改成功，3秒钟后跳转到登录页面！", {icon: 6},  function () {
                        $.removeCookie("id", {domain:"localhost", path:"/crm"});
                        $.removeCookie("userName", {domain:"localhost", path:"/crm"});
                        $.removeCookie("trueName", {domain:"localhost", path:"/crm"});

                        window.parent.location.href=ctx+"/index";
                    });
                } else {
                    layer.msg(resp.msg, {icon: 5});
                }
            }
        });

        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });

});