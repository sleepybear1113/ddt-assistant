Date.prototype.Format = function (fmt) {
    let o = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S": this.getMilliseconds(),
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (let k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }
    return fmt;
}

function showInfo(msg) {
    let textareaInfo = window.top.document.getElementById("textarea-info");
    textareaInfo.value = "";
    setTimeout(() => {
        textareaInfo.value = `${new Date().Format("yyyy-MM-dd hh:mm:ss")}\n${msg}`;
    }, 100);
}

function adjustSize(iframe) {
    iframe.height = document.documentElement.clientHeight;
}
