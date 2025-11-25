<template>
  <NavBar />
  <router-view />
  <Footer />
  <BackToTop />
  
</template>

<script setup>
import NavBar from './components/NavBar.vue'
import Footer from './components/Footer.vue'
import BackToTop from './components/BackToTop.vue'
</script>

<style>

</style>
