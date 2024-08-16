$(function () {
    // 加载树形结构
    loadModuleData();
});

let rid = $("[name=roleId]").val();

function loadModuleData() {

    var setting = {
        check: {
            enable: true
        },
        data: {
            simpleData: {
                enable: true
            }
        },
        callback: {
            onCheck: zTreeOnCheck
        }
    };

    $.ajax({
        type: 'POST',
        url: ctx+'/module/list?rid=' + rid,
        dataType: "json",//dataType:"jsonp",//jsonp是跨域方式
        success: function (resp) {
            // console.log(resp);
            if (resp.code == 200) {
                // console.log(resp);
                $.fn.zTree.init($("#test1"), setting, resp.result);
            }
        }
    });
}

// { id:113, pId:11, name:"叶子节点113"}

/**
 * 复选框勾选回调函数
 * @param event
 * @param treeId
 * @param treeNode
 */
function zTreeOnCheck(event, treeId, treeNode) {
    // alert(treeNode.tId + ", " + treeNode.name + "," + treeNode.checked);
    /*var treeObj = $.fn.zTree.getZTreeObj("test1");
    var nodes = treeObj.getChangeCheckedNodes();*/
    var treeObj = $.fn.zTree.getZTreeObj("test1");
    var nodes = treeObj.getCheckedNodes(true);
    console.log(nodes);

    let mids="";

    for (let i = 0; i < nodes.length; i++) {
        if (i == (nodes.length - 1)) {
            mids += "mid=" + nodes[i].id;
        } else {
            mids += "mid=" + nodes[i].id +"&";
        }
    }

    console.log(mids);
    console.log(rid);
    let url1;

    $.ajax({
        type: 'POST',
        url: ctx + '/Permission/add?' + mids + '&rid=' + rid,
        success: function (resp) {
            console.log(resp);
        }
    });
};