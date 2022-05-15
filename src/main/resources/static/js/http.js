Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

function fillUserConfig(userConfig) {
    if (userConfig == null) {
        return;
    }
    document.getElementById("input-username").value = userConfig.username;
    document.getElementById("input-password").value = userConfig.password;
    document.getElementById("input-softId").value = userConfig.softId;
    document.getElementById("input-captureInterval").value = userConfig.captureInterval;
    document.getElementById("input-keyPressAfterCaptchaShow").value = userConfig.keyPressAfterCaptchaShow;
    document.getElementById("input-keyPressAfterCaptchaDisappear").value = userConfig.keyPressAfterCaptchaDisappear;
    document.getElementById("input-keyPressDelayAfterCaptchaDisappear").value = userConfig.keyPressDelayAfterCaptchaDisappear;
    document.getElementById("input-defaultChoiceAnswer").value = userConfig.defaultChoiceAnswer;
    document.getElementById("input-timeout").value = userConfig.timeout;
    document.getElementById("input-logPrintInterval").value = userConfig.logPrintInterval;
    document.getElementById("input-detectNewWindowInterval").value = userConfig.detectNewWindowInterval;
}

function initUserConfig() {
    let url = "config/get"
    axios.get(url).then((res) => {
        let userConfig = new UserConfig(res.data.result);
        fillUserConfig(userConfig);
    });
}

function resetUserConfig() {
    if (!confirm("确定要重置？")) {
        return;
    }
    let url = "config/reset";
    axios.get(url).then((res) => {
        let userConfig = new UserConfig(res.data.result);
        fillUserConfig(userConfig);
        showInfo("成功");
    });
}

function saveUserConfig() {
    let userConfig = new UserConfig();
    userConfig.username = document.getElementById("input-username").value;
    userConfig.password = document.getElementById("input-password").value;
    userConfig.softId = document.getElementById("input-softId").value;
    userConfig.captureInterval = document.getElementById("input-captureInterval").value;
    userConfig.keyPressAfterCaptchaShow = document.getElementById("input-keyPressAfterCaptchaShow").value;
    userConfig.keyPressAfterCaptchaDisappear = document.getElementById("input-keyPressAfterCaptchaDisappear").value;
    userConfig.keyPressDelayAfterCaptchaDisappear = document.getElementById("input-keyPressDelayAfterCaptchaDisappear").value;
    userConfig.defaultChoiceAnswer = document.getElementById("input-defaultChoiceAnswer").value;
    userConfig.timeout = document.getElementById("input-timeout").value;
    userConfig.logPrintInterval = document.getElementById("input-logPrintInterval").value;
    userConfig.detectNewWindowInterval = document.getElementById("input-detectNewWindowInterval").value;

    let url = "config/update"
    console.log(userConfig);
    axios.post(url, userConfig).then(() => {
        showInfo("成功");
    });
}

function bindAll() {
    let url = "captcha/addAllCaptcha"
    showInfo("正在检测窗口...")
    axios.get(url).then((res) => {
        let bindResultVo = new BindResultVo(res.data.result);
        if (bindResultVo == null) {
            return;
        }

        showInfo(`新增线程数：${bindResultVo.newAddCount}，维持运行线程数：${bindResultVo.runningCount}`);
    });
}

function testCaptcha() {
    let url = "captcha/test";
    showInfo("识别中，请稍后...");
    axios.get(url).then((res) => {
        let tjResponse = new TjResponse(res.data.result);
        if (tjResponse == null) {
            showInfo("失败：接口返回内容为空");
            return;
        }

        showInfo(tjResponse.info());
    });
}

function showInfo(msg) {
    let textareaInfo = document.getElementById("textarea-info");
    textareaInfo.value = "";
    setTimeout(() => {
        textareaInfo.value = `${new Date().Format("yyyy-MM-dd hh:mm:ss")}\n${msg}`;
    }, 100);
}