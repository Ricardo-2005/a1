<script setup lang="ts">
defineProps<{ title: string; subtitle?: string }>()
</script>

<template>
  <div class="page-header">
    <div>
      <h2 class="section-title">{{ title }}</h2>
      <p v-if="subtitle" class="muted">{{ subtitle }}</p>
    </div>
    <div class="page-header__actions">
      <slot />
    </div>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

.page-header__actions {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

p {
  margin: 0.4rem 0 0;
}
</style>
