<template>
  <div class="crumbs" v-if="items.length">
    <span v-for="(it, idx) in items" :key="idx" class="item" @click="goCategory(it)">
      {{ it.name }}<span v-if="idx < items.length - 1"> / </span>
    </span>
  </div>
</template>

<script>
import { onMounted, watch, ref } from 'vue'
import { path as fetchPath } from '../api/categories'
import { useRouter } from 'vue-router'

export default {
  props: { categoryId: { type: Number, default: 0 } },
  setup(props) {
    const items = ref([])
    const router = useRouter()
    async function load() {
      if (!props.categoryId) { items.value = []; return }
      try { const res = await fetchPath(props.categoryId); items.value = res.data || [] } catch { items.value = [] }
    }
    function goCategory() { router.push('/') }
    onMounted(load)
    watch(() => props.categoryId, load)
    return { items, goCategory }
  }
}
</script>

<style scoped>
.crumbs { padding: 12px 16px; color: #666; }
.item { cursor: pointer; }
</style>
