<template>
  <div class="app-wrapper">
    <el-container>
      <el-aside width="200px" class="sidebar-container"><Menu/></el-aside>
      <el-container>
        <el-header><Header/></el-header>
        <el-main>
          <Tabs/>
          <router-view/>
        </el-main>
        <el-footer><Footer/></el-footer>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import Menu from "@/layout/menu/index.vue"
import Header from "@/layout/header/index.vue"
import Footer from "@/layout/footer/index.vue"
import Tabs from "@/layout/tabs/index.vue"

</script>

<style lang="scss" scoped>
.app-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}
.sidebar-container {
  background-color: #2d3a4b;
  height: 100%;
}
.el-container{
  height:100%
}
.el-header{
  padding-left: 0px;
  padding-right: 0px;
}
:deep(ul.el-menu){
  border-right-width: 0px
}
</style>