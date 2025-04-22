import {createStore} from 'vuex'
import router from '@/router'

export default createStore({
    state: {
        token: sessionStorage.getItem("token") || "",  // ✅ 初始值从 sessionStorage 取
        menuList: JSON.parse(sessionStorage.getItem("menuList")) || [],
        userInfo: JSON.parse(sessionStorage.getItem("userInfo")) || {},
        hasRoutes: false,  // 动态路由标识，默认false，没有路由

        editableTabsValue: "/index",
        editableTabs: [
            {
                title: '首页',
                name: 'index'
            }
        ]
    },
    getters: {
        GET_TOKEN: state => state.token, // ✅ 直接从 state 取
        GET_MENULIST: state => state.menuList,
        GET_USERINFO: state => state.userInfo
    },
    mutations: {
        SET_TOKEN: (state, token) => {
            state.token = token;  // ✅ 先存到 Vuex 里
            sessionStorage.setItem("token", token);
        },
        SET_MENULIST: (state, menuList) => {
            state.menuList = menuList;
            sessionStorage.setItem("menuList", JSON.stringify(menuList));
        },
        SET_USERINFO: (state, userInfo) => {
            state.userInfo = userInfo;
            sessionStorage.setItem("userInfo", JSON.stringify(userInfo));
        },
        SET_ROUTES_STATE: (state, hasRoutes) => {
            state.hasRoutes = hasRoutes;
        },
        ADD_TABS: (state, tab) => {
            // Tabs中没有，就添加进去
            if (state.editableTabs.findIndex(e => e.name === tab.path) === -1) {
                state.editableTabs.push({
                    title: tab.name,
                    name: tab.path
                })
            }
            // 选中当前的tab
            state.editableTabsValue = tab.path
        },
        RESET_TABS: (state, tab) => {
            state.editableTabsValue = "/index"
            state.editableTabs = [
                {
                    title: '首页',
                    name: 'index'
                }
            ]
        }
    },
    actions: {
        logout({commit}) {
            commit("SET_TOKEN", "");
            commit("SET_MENULIST", []);
            commit("SET_USERINFO", {});
            sessionStorage.clear();
            router.replace("/login");
        }
    }
});

