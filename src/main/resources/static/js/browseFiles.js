let browseFilesApp = new Vue({
    el: '#browseFiles',
    data: {
        fileList: [new FileInfoVo()],
        currentPath: "./",
        picSuffix: [".jpg", ".png", ".bmp"],
    },
    created() {
        this.fileList = [];

    },
    methods: {
        getLocalFileWithDir(dir) {
            let url = "system/getLocalFileWithDir";
            let params = {
                "params": {
                    dir: dir,
                }
            };

            this.fileList = [];
            this.currentPath = dir;
            axios.get(url, params).then((res) => {
                let fileList = res.data.result;
                if (fileList == null || fileList.length === 0) {
                    return;
                }

                for (let i = 0; i < fileList.length; i++) {
                    this.fileList.push(new FileInfoVo(fileList[i]));
                }
            });
        },
        getReadableFileSizeString(size) {
            return getReadableFileSizeString(size);
        },
        browseFile(path) {
            let isPic = false;
            for (let i = 0; i < this.picSuffix.length; i++) {
                if (path.endsWith(this.picSuffix[i])) {
                    isPic = true;
                    break;
                }
            }

            if (isPic) {
                showImg(path);
                return;
            }

            window.open(path);
        },
        browseFile2(path) {
            let isPic = false;
            for (let i = 0; i < this.picSuffix.length; i++) {
                if (path.endsWith(this.picSuffix[i])) {
                    isPic = true;
                    break;
                }
            }

            return path;
        },
        getTodayIncome() {
            let today = new Date().Format("yyyyMMdd");
            this.currentPath = "./图片/副本翻牌截图/" + today + "/";
            this.getLocalFileWithDir(this.currentPath);
        },
    }
});