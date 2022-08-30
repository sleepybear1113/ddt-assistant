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

function showInfo(msg, timeout = 100) {
    let textareaInfo = window.top.document.getElementById("textarea-info");
    textareaInfo.value = "";
    setTimeout(() => {
        textareaInfo.value = `${new Date().Format("yyyy-MM-dd hh:mm:ss")}\n${msg}`;
    }, timeout);
}

function hideImg() {
    document.getElementById("large-pic-div").style.display = "none";
    document.getElementById("cover").style.display = "none";
}

function showImg(src) {
    document.getElementById("cover-img").setAttribute("src", src);
    document.getElementById("large-pic-div").style.display = "";
    document.getElementById("cover").style.display = "";
}

function jumpToMailSettingPage() {
    tabApp.changeTab("setting");
    settingApp.selectedTab = '邮箱设置';
}

function showGameShot(hwnd, imgQuality) {
    let url = "dm/getGameScreenPath";
    axios.get(url, {params: {hwnd: hwnd, imgQuality: imgQuality}}).then((res) => {
        showImg(res.data.result.string);
    });
}
