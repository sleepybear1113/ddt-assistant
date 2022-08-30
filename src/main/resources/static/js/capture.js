let captureApp = new Vue({
    el: '#capture',
    data: {
        msg: "",
        src: "",
        width: "50%",
        hwnds: [],
        intervalList: [100, 200, 300, 500, 1000, 1500, 2000, 3000],
        interval: null,
        intervalRes: null,
        imgQuality: 5,
        imgQualityList: [1, 3, 5, 7, 9, 10],
    },
    created() {
    },
    methods: {
        changeInterval: function (i) {
            this.interval = i;
            if (this.interval <= 50) {
                this.interval = 50;
            }
        },
        changeImgQuality: function (i) {
            this.imgQuality = i;
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
        getGameShot: function (hwnd) {
            let url = "dm/getGameScreenPath";
            this.getImgPath(url, hwnd, this.imgQuality);
        },
        getScreenshotPath: function () {
            let url = "capture/getScreenshotPath";
            this.getImgPath(url, null, this.imgQuality, "屏幕截图");
        },
        getImgPath: function (url, hwnd, imgQuality, msg) {
            this.msg = "";
            clearInterval(this.intervalRes);
            let params = {params: {hwnd: hwnd, imgQuality: imgQuality}};
            axios.get(url, params).then((res) => {
                this.src = res.data.result.string;
                this.msg = hwnd == null ? msg : hwnd;
            });
            if (this.interval == null) {
                return;
            }
            this.intervalRes = setInterval(() => {
                axios.get(url, params).then((res) => {
                    this.src = res.data.result.string;
                    this.msg = hwnd == null ? msg : hwnd;
                });
            }, this.interval);

        },
        clearIntervalFunc: function () {
            this.interval = null;
            clearInterval(this.intervalRes);
        },
        changeImgSize: function (width) {
            if (width.startsWith("-") || width.startsWith("+")) {
                width = parseInt(this.width.replace("%", "")) + parseInt(width);
            }
            this.width = width + "%";
        },
    }
});