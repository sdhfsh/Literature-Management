import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import zhCn from 'element-plus/es/locale/lang/zh-cn' // 引入中文语言包
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import '@/assets/styles/border.css'
import '@/assets/styles/reset.css'

import SvgIcon from '@/icons'

// 导入路由守卫功能
import '@/router/permission'

const app = createApp(App)
SvgIcon(app)
app.use(store)
app.use(router)
app.use(ElementPlus, {
    locale: zhCn,
})
app.mount('#app')
