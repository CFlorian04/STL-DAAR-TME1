<template>
  <main>
    <div id="search-bar">
      <h3>Find Books</h3>
      <input type="text" placeholder="Search..." />
    </div>
    <div id="lang-selection">
      <!-- <div class="language" v-for="lang in $store.state.languages" v-bind:key="lang">
        <router-link :to="'lang/' + lang">{{ lang }}</router-link>
      </div> -->
    </div>
    <RouterView />
  </main>
</template>
