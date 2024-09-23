<template>
    <header>
        <h1>Gutenberg Project</h1>
        <div id="app">
            <img src="@/assets/logoGutenberg_black.gif" alt="Logo Gutenberg" />
        </div>
    </header>
    
</template>
    
<script>
    export default {
        name: 'LogoGutemberg'
    }
</script>

<style scoped>
#app {
    display: flex;
    justify-content: center;
    align-items: center;
}

h1 {
    font-size: 70px;
}

img {
    max-width: 50%;
    height: auto;
}
</style>