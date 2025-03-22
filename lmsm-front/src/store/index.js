import { createStore } from 'vuex'

export default createStore({
  state: {
  },
  getters: {
    GET_TOKEN:state => {
      return sessionStorage.getItem("token")
    },
    GET_MENULIST:state => {
      return JSON.parse(sessionStorage.getItem("menuList"));
    }
  },
  mutations: {
    SET_TOKEN:(state,token)=>{
      sessionStorage.setItem("token",token);
    },
    SET_MENULIST:(state,menuList)=>{
      sessionStorage.setItem("menuList",JSON.stringify(menuList));
    }
  },
  actions: {
  },
  modules: {
  }
})
