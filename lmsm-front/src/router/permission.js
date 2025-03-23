import router from '@/router/index'
import store from "@/store/index"

// 路由守卫
router.beforeEach((to, from, next) => {
    const whiteList = ['/login', '/register']   // 白名单，无论是否含有token，都可以访问
    let token = store.getters.GET_TOKEN
    console.log("permissionToken = " + token)

    let hasRoutes = store.state.hasRoutes;
    let menuList = store.getters.GET_MENULIST;

    console.log("menuList = " + menuList)
    if (token) {
        // 登陆成功之后无法访问'/login', '/register'
        // if (whiteList.includes(to.path)) {
        //     next("/")
        // } else {
        //
        // }
        if (!hasRoutes) {
            bindRoute(menuList)
            store.commit("SET_ROUTES_STATE", true)
        }
        next()
    } else {
        if (whiteList.includes(to.path)) {
            next()
        } else {
            next("login")
        }
    }
})

// 动态绑定路由
const bindRoute = (menuList) => {
    let newRoutes = router.options.routes;
    menuList.forEach(menu => {
        if (menu.children) {
            menu.children.forEach(child => {
                let route = menuToRoute(child, menu.name)
                if (route) {
                    newRoutes[0].children.push(route);
                }
            })
        }
    })
    // 重新添加到路由管理
    newRoutes.forEach(route => {
        router.addRoute(route)
    })
}

// 菜单对象转为路由对象
const menuToRoute = (menu, parentName) => {
    if (!menu.component) {
        // menu.component不存在
        return null;
    } else {
        let route = {
            name: menu.name,
            path: menu.path,
            meta: {
                parentName: parentName,
            }
        }
        route.component = () => import('@/views/' + menu.component + '.vue')
        return route;
    }
}

