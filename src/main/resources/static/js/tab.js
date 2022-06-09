let tabApp = new Vue({
    el: '#tab',
    data: {
        tabs: [
            {tabId: "captcha", name: "验证码功能"},
            {tabId: "auction", name: "拍卖场功能"},
            {tabId: "test", name: "测试功能正常"},
            {tabId: "setting", name: "设置"},
            {tabId: "introduction", name: "相关介绍"},
        ]
    },
    created() {
        this.changeTab(this.tabs[0].tabId);
    },
    methods: {
        changeTab: function (tabName) {
            let tabs = document.getElementsByClassName("tab");
            for (let i = 0; i < tabs.length; i++) {
                let tab = tabs[i];
                let id = tab.id;
                if (id === tabName) {
                    tab.style.display = "";
                } else {
                    tab.style.display = "none";
                }
            }
        }
    }
});