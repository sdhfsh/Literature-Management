import {createRouter, createWebHistory} from 'vue-router'

const routes = [
    {
        path: '/',
        name: '主页',
        component: () => import('../layout'),
        redirect: '/index',
        children: [
            {
                path: '/index',
                name: '首页',
                component: () => import('@/views/index/index')
            }, {
                path: '/userCenter',
                name: '个人中心',
                component: () => import('@/views/userCenter/index')
            },
        ]
    },
    {
        path: '/test',
        name: '测试',
        component: () => import('@/views/test/index.vue')
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('@/views/Login.vue')
    },
    {
        path: '/register',
        name: 'register',
        component: () => import('@/views/Register.vue')
    }
    // ,
    // {
    //     path: '/:pathMatch(.*)*',  // 通配符匹配所有未定义的路径
    //     name: 'NotFound',
    //     component: () => import('../views/error/404.vue') // 你的 404 页面
    // }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
