<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import HeaderTitleItem from './components/HeaderTitleItem.vue'
</script>

<template>
  <div>
    <nav>
      <div class="wrapper">
        <HeaderTitleItem />

        <div class="nav-links">
          <RouterLink to="/">Home</RouterLink>
          <RouterLink to="/search">Search</RouterLink>
          <RouterLink to="/about">About</RouterLink>
        </div>
      </div>
    </nav>

    <main>
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
nav {
  line-height: 1.5;
  max-height: 100vh;
  width: 100%;
  font-size: 12px;
  text-align: center;
  margin-top: 2rem;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

.nav-links a.router-link-exact-active {
  color: var(--color-text);
}

.nav-links a.router-link-exact-active:hover {
  background-color: transparent;
}

.nav-links a {
  display: inline-block;
  padding: 0 1rem;
  border-left: 1px solid var(--color-border);
}

.nav-links a:first-of-type {
  border: 0;
}

@media (min-width: 1024px) {
  nav {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  nav .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }

  .nav-links {
    text-align: left;
    margin-left: -1rem;
    font-size: 1rem;
    padding: 1rem 0;
    margin-top: 1rem;
  }
}
</style>
