layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    // 表单中的submit事件（注意这里是submit，不能是其他的，除非是其他事件），login为表单登录按钮的lay-filter
    // 这个data中的数据是表单中的信息！
    form.on('submit(login)', function(data){

        // 使用另一个js文件的工具类，必须在同一个页面中进行引入
        // 这里index.js被使用的地方法是index.ftl，所以index.ftl也要引入util.js文件，这样index.js中才可以用util.js中的方法！否则即使写的时候有提示也无效！
        if (isEmpty(data.field.username)) {
            layer.msg("用户名称不能为空！", {icon: 5});
            return false;
        } else if (isEmpty(data.field.password)) {
            layer.msg("用户密码不能为空！", {icon:5});
            return false;
        }


        // POST JSON访问方式
        $.ajax({
            type: 'POST',
            url: ctx+"/user/login",
            // data中的数据是username和password，这两个名字是html中相关标签的name值
            // userName、userPwd是后端接受参数方法的参数名
            data: {
                userName: data.field.username,
                userPwd: data.field.password
            },
            // 注意这里的data不是form.on中的data，这里的data中的数据是后端传过来的json格式数据
            // 把data改为data1验证猜想
            success: function (data1) {
                // console.log(1);
                // console.log($("#rememberMe").val());

                // 写下面代码的时候，先打印一编data1，就知道下面的参数要怎么写了
                // console.log(data1);
                if (data1.code == 200) {

                    // if (data.field.rememberMe) {
                    // 这里必须用if...else...两者中只选一个
                    if (data.field.rememberMe) {
                        // 如果勾选了记住密码的复选框，按指定时间存储存cookie
                        $.cookie("id", data1.result.id, {expires: 7});
                        $.cookie("userName", data1.result.userName, {expires: 7});
                        $.cookie("trueName", data1.result.trueName, {expires: 7});
                    } else {
                        // 存cookie
                        $.cookie("id", data1.result.id);
                        $.cookie("userName", data1.result.userName);
                        $.cookie("trueName", data1.result.trueName);
                    }

                    // 页面跳转
                    window.location.href = ctx + "/main";
                } else {
                    layer.msg(data1.msg, {icon: 5});
                }
            }
        });

        //阻止表单跳转。如果需要表单跳转，去掉这段即可
        return false;
    });


});