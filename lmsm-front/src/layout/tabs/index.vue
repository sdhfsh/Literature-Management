<template>
  <el-tabs
      v-model="editableTabsValue"
      type="card"
      class="demo-tabs"
      closable
      @tab-remove="removeTab"
      @tab-click="clickTab"
  >
    <el-tab-pane
        v-for="item in editableTabs"
        :key="item.name"
        :label="item.title"
        :name="item.name"
    >
      {{ item.content }}
    </el-tab-pane>
  </el-tabs>
</template>

<script setup>
import {ref, watch} from 'vue'
import store from '@/store'
import {useRoute, useRouter} from "vue-router";

const router = useRouter();

const editableTabsValue = ref(store.state.editableTabsValue)
const editableTabs = ref(store.state.editableTabs)

const removeTab = (targetName) => {
  const tabs = editableTabs.value
  let activeName = editableTabsValue.value

  if (activeName === '/index') {
    return
  }

  if (activeName === targetName) {
    tabs.forEach((tab, index) => {
      if (tab.name === targetName) {
        const nextTab = tabs[index + 1] || tabs[index - 1]
        if (nextTab) {
          activeName = nextTab.name
        }
      }
    })
  }

  editableTabsValue.value = activeName
  editableTabs.value = tabs.filter((tab) => tab.name !== targetName)

  store.state.editableTabsValue = editableTabsValue.value
  store.state.editableTabs = editableTabs.value

  router.push({ path: activeName })
}

const refreshTabs = () => {
  editableTabsValue.value = store.state.editableTabsValue
  editableTabs.value = store.state.editableTabs
}

const clickTab = (target) => {
  console.log(target);
  router.push({name: target.props.label});
}

watch(store.state, () => {
  refreshTabs()
}, {deep: true, immediate: true})

</script>

<style>
.el-tabs--card > .el-tabs__header .el-tabs__item {
  padding: 6px 10px !important; /* 调整标签大小 */
  font-size: 12px !important; /* 字体稍小 */
  border-radius: 4px !important; /* 圆角美化 */
}

.el-tabs {
  height: 35px !important;
}

.el-tabs__header {
  height: 35px !important;
}

/* 选中的标签样式 */
.el-tabs--card > .el-tabs__header .el-tabs__item.is-active {
  background-color: #409EFF !important; /* 选中项的背景色（更浅的蓝色） */
  color: white !important; /* 选中项的文字颜色 */
}
</style>
