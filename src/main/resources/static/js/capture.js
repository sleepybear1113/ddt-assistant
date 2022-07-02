let captureApp = new Vue({
    el: '#capture',
    data: {
        msg: "",
        src: "",
        width: "50%",
        hwnds: [],
    },
    created() {
    },
    methods: {
        getScreenshotPath: function () {
            this.msg = "";
            let url = "capture/getScreenshotPath"
            axios.get(url).then((res) => {
                let stringRet = new StringRet(res.data.result);
                this.src = stringRet.msg;
                this.msg = "屏幕截图";
            });
        },
        getAllGamePicPath: function () {
            showInfo("功能未完待续...");
        },
        getDdtHwnds: function () {
            this.hwnds = [];
            let url = "dm/getDdtHwnds";
            axios.get(url).then((res) => {
                let list = res.data.result;
                for (let i = 0; i < list.length; i++) {
                    this.hwnds.push(list[i]);
                }
            });
        },
        showImg: function (hwnd) {
            let url = "dm/getGameScreenPath";
            this.msg = "";
            axios.get(url, {params: {hwnd: hwnd}}).then((res) => {
                this.src = res.data.result.string;
                this.msg = hwnd;
            });
        },
        changeImgSize: function (width) {
            if (width.startsWith("-") || width.startsWith("+")) {
                width = parseInt(this.width.replace("%", "")) + parseInt(width);
            }
            this.width = width + "%";
        },
    }
});