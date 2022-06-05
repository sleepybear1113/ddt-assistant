let testApp = new Vue({
    el: '#test',
    data: {
        test: {
            testDelay: "500",
            clickX: "530",
            clickY: "585",
            keyPress: "F5",
        },
    },
    created() {
    },
    methods: {
        testClick: function () {
            let url = "test/click";
            axios.get(url, {
                params: {
                    x: this.test.clickX,
                    y: this.test.clickY,
                    delay: this.test.testDelay
                }
            }).then((res) => {
                let testRes = new TestRes(res.data.result);
                if (testRes.success) {
                    showInfo(`已点击${testRes.msg}, 请检查游戏中是否点击到了对应坐标的位置`);
                } else {
                    showInfo(`点击失败，${testRes.msg}`);
                }
            });
        },
        testKeyPress: function () {
            let url = "test/keyPress";
            axios.get(url, {
                params: {
                    key: this.test.keyPress,
                    delay: this.test.testDelay
                }
            });
        },
    }
});