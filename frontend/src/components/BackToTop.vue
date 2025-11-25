<template>
  <el-button v-show="visible" class="btn" circle type="primary" @click="toTop">↑</el-button>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
const visible = ref(false)
function onScroll() { visible.value = window.scrollY > 200 }
function toTop() { window.scrollTo({ top: 0, behavior: 'smooth' }) }
onMounted(() => window.addEventListener('scroll', onScroll))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.btn { position: fixed; right: 24px; bottom: 24px; }
</style>
