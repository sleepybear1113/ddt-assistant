let reCaptureApp = new Vue({
    el: '#reCapture',
    data: {
        width: "50%",
        hwnds: [],
        templates: [],
        templateInfoList: [],
    },
    created() {
        this.getAllTemplateInfoList();
    },
    methods: {
        openResourceDir: function () {
            let path = "资源图片/模板/";
            this.openDir(path, false);
        },
        openDir: function (path, select) {
            let url = "system/openWithExplorer";
            axios.get(url, {params: {path: path, select: select}}).then((res) => {
            });
        },
        getDdtHwnds: function () {
            this.hwnds = [];
            let url = "dm/getDdtHwnds";
            axios.get(url).then((res) => {
                let list = res.data.result;
                for (let i = 0; i < list.length; i++) {
                    this.hwnds.push({"hwnd": list[i], "enabled": false});
                }
            });
        },
        convertToOfficialTemplate: function (path) {
            let url = "reCapture/convertToOfficialTemplate";
            axios.get(url, {params: {path: path}}).then((res) => {
                showInfo(res.data.result);
            });
        },
        getAllTemplateInfoList: function () {
            this.templateInfoList = [];
            let url = "reCapture/getAllTemplateInfoList";
            axios.get(url).then((res) => {
                let list = res.data.result;
                for (let i = 0; i < list.length; i++) {
                    this.templateInfoList.push(new RecaptureDomain(list[i]));
                }
            });
        },
        showTemplates: function (templatePrefix) {
            let url = "reCapture/getTemplates";
            this.templates = [];
            axios.get(url, {params: {templatePrefix: templatePrefix}}).then((res) => {
                let list = res.data.result;
                if (list == null || list.length === 0) {
                    showInfo("模板获取为空");
                    return;
                }
                for (let listKey in list) {
                    let item = list[listKey];
                }
                this.templates = list;
            });
        },
        getGameShot: function (hwnd) {
            let url = "dm/getGameScreenPath";
            axios.get(url, {params: {hwnd: hwnd}}).then((res) => {
                showImg(res.data.result.string);
            });
        },
        deleteSampleImg: function (template) {
            if (!confirm("确定要删除吗？")) {
                return;
            }
            let url = "reCapture/deleteSampleImg";
            axios.get(url, {params: {src: [template].map(t => t).join(',')}}).then((res) => {
                let b = res.data.result === true;
                showInfo(b ? "成功" : "失败");
                if (b) {
                    this.src = "";
                    this.msg = "";
                }
            });
            for (let i = 0; i < this.templates.length; i++) {
                let item = this.templates[i];
                if (item === template) {
                    this.templates.splice(i, 1);
                    break;
                }
            }
        },
        reCaptureSampleImg: function (msg, url) {
            this.msg = "";
            this.recaptureDeleteButtonShown = false;
            let hwnds = [];
            for (let hwndsKey in this.hwnds) {
                let item = this.hwnds[hwndsKey];
                if (item.enabled) {
                    hwnds.push(item.hwnd);
                }
            }
            if (hwnds.length === 0) {
                showInfo("没有选中的句柄");
                return;
            }
            axios.get(url, {params: {hwnds: hwnds.map(t => t).join(',')}}).then((res) => {
                let src = res.data.result;
                showInfo(src);
                this.templates = src;
            });
        },
    }
});