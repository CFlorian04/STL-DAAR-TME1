<template>
  <div class="about">
    <h1>À propos de ce projet</h1>

    <nav>
      <a class="button" href="https://www-npa.lip6.fr/~buixuan/daar2024" target="_blank">DAAR</a>
      <a class="button" href="https://www.gutenberg.org/" target="_blank">Gutenberg Project</a>
      <a class="button" href="https://github.com/CFlorian04/STL-DAAR-TME1" target="_blank">Github</a>
    </nav>


  </div>
</template>

<style>
@media (min-width: 1024px) {
  .about {
    min-height: 100vh;
    display: flex;
    align-items: center;
  }
}

a.button {
  padding: 1px 6px;
  border: 1px outset buttonborder;
  border-radius: 3px;
  color: var(--color-text);
  background-color: var(--color-background);
  text-decoration: none;
}

nav {
  width: 100%;
  font-size: 12px;
  text-align: center;
  margin-top: 2rem;
}

</style>
