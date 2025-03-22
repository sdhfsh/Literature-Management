import {createRouter, createWebHistory} from 'vue-router'

const routes = [
    {
        path: '/test',
        name: '测试',
        component: () => import('@/views/test/index.vue')
    },
    {
        path: '/',
        name: '首页',
        component: () => import('../layout')
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('../views/user/Login.vue')
    },
    {
        path: '/register',
        name: 'register',
        component: () => import('../views/user/Register.vue')
    },
    {
        path: '/:pathMatch(.*)*',  // 通配符匹配所有未定义的路径
        name: 'NotFound',
        component: () => import('../views/error/404.vue') // 你的 404 页面
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
