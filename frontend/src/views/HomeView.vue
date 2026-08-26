<template>
  <div class="home-shell">
    <header class="site-header">
      <div class="header-inner">
        <router-link to="/" class="brand" aria-label="Inicio">
          <span class="brand-mark">DTP</span>
          <span><strong>Dario Torregroza Perez</strong><small>Plataforma institucional</small></span>
        </router-link>
        <nav class="site-nav" :class="{ open: menuAbierto }">
          <a href="#institucion" @click="menuAbierto = false">Institución</a>
          <a href="#noticias" @click="menuAbierto = false">Noticias</a>
          <a href="#agenda" @click="menuAbierto = false">Agenda</a>
          <a href="#contacto" @click="menuAbierto = false">Contacto</a>
          <router-link to="/login" class="nav-action" @click="menuAbierto = false">Acceso institucional</router-link>
        </nav>
        <button class="menu-button" aria-label="Abrir menú" @click="menuAbierto = !menuAbierto">☰</button>
      </div>
    </header>

    <main>
      <section class="hero">
        <div class="hero-copy">
          <p class="eyebrow">Educación que transforma</p>
          <h1>Dario Torregroza Perez</h1>
          <p class="hero-lead">Una comunidad educativa que aprende, convive y construye futuro desde el territorio.</p>
          <div class="hero-actions"><a href="#institucion" class="primary-button">Conoce nuestra institución <span>↘</span></a><a href="#noticias" class="text-link">Ver noticias</a></div>
        </div>
        <div class="hero-note"><span>01</span><p>Formación integral<br>con sentido humano</p></div>
      </section>

      <section id="institucion" class="intro-section section-wrap">
        <div class="section-kicker">Nuestra institución</div>
        <div class="intro-grid">
          <div><h2>Crecer juntos,<br><em>llegar más lejos.</em></h2></div>
          <div><p class="intro-text">{{ info.descripcion || 'Somos una institución educativa comprometida con el desarrollo integral de nuestros estudiantes y el fortalecimiento de nuestra comunidad.' }}</p><a href="#mision-vision" class="inline-link">Conoce nuestro propósito <span>→</span></a></div>
        </div>
      </section>

      <section id="mision-vision" class="purpose-section">
        <div class="section-wrap purpose-grid">
          <article><span class="number">01</span><h3>Misión</h3><p>{{ info.mision || 'Nuestra misión será publicada próximamente.' }}</p></article>
          <article><span class="number">02</span><h3>Visión</h3><p>{{ info.vision || 'Nuestra visión será publicada próximamente.' }}</p></article>
          <article><span class="number">03</span><h3>Valores</h3><p>{{ info.valores || 'Respeto, responsabilidad, solidaridad y excelencia.' }}</p></article>
        </div>
      </section>

      <section id="noticias" class="section-wrap content-section">
        <div class="section-heading"><div><div class="section-kicker">Lo que está pasando</div><h2>Actualidad</h2></div><span class="heading-line"></span></div>
        <div v-if="noticias.length" class="news-grid"><article v-for="noticia in noticias.slice(0, 3)" :key="noticia.id" class="news-card"><div class="news-date">{{ fecha(noticia.fechaPublicacion) }}</div><h3>{{ noticia.titulo }}</h3><p>{{ noticia.resumen || noticia.contenido }}</p><span class="card-arrow">↗</span></article></div>
        <p v-else class="empty-state">Próximamente encontrarás aquí las noticias de nuestra comunidad.</p>
      </section>

      <section id="agenda" class="agenda-section"><div class="section-wrap"><div class="section-heading light"><div><div class="section-kicker">No te lo pierdas</div><h2>Próximos eventos</h2></div></div><div v-if="eventos.length" class="event-list"><article v-for="evento in eventos.slice(0, 3)" :key="evento.id"><time><strong>{{ dia(evento.fecha) }}</strong><span>{{ mes(evento.fecha) }}</span></time><div><h3>{{ evento.titulo }}</h3><p>{{ evento.lugar || 'Información institucional' }}<span v-if="evento.horaInicio"> · {{ evento.horaInicio }}</span></p></div><span class="card-arrow">→</span></article></div><p v-else class="empty-state light-text">No hay eventos próximos publicados.</p></div></section>
      <section id="contacto" class="contact-section"><div class="section-wrap contact-grid"><div><div class="section-kicker">Estamos para escucharte</div><h2>Hablemos.</h2></div><div><p>{{ info.descripcion || 'Comunícate con nuestra institución para conocer nuestra oferta y resolver tus inquietudes.' }}</p><p v-if="info.telefonoGeneral">☎ {{ info.telefonoGeneral }}</p><p v-if="info.correoGeneral">✉ {{ info.correoGeneral }}</p><p v-if="info.sitioWeb"><a :href="info.sitioWeb" target="_blank" rel="noreferrer">{{ info.sitioWeb }}</a></p></div></div></section>
    </main>

    <footer class="site-footer"><div class="section-wrap footer-inner"><div class="brand footer-brand"><span class="brand-mark">DTP</span><span><strong>Dario Torregroza Perez</strong><small>Plataforma institucional</small></span></div><p>Educación con propósito.</p><router-link to="/login" class="footer-login">Acceso institucional →</router-link></div></footer>
    <ChatWidget />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import api from '../services/api'
import ChatWidget from '../components/chatbot/ChatWidget.vue'

const info = ref({})
const noticias = ref([])
const eventos = ref([])
const menuAbierto = ref(false)
const fecha = (valor) => valor ? new Date(valor).toLocaleDateString('es-CO', { day: '2-digit', month: 'short', year: 'numeric' }) : 'Actualidad'
const dia = (valor) => valor ? new Date(`${valor}T00:00:00`).getDate() : '--'
const mes = (valor) => valor ? new Date(`${valor}T00:00:00`).toLocaleDateString('es-CO', { month: 'short' }).replace('.', '').toUpperCase() : 'PRÓX.'

onMounted(async () => {
  const respuestas = await Promise.allSettled([api.get('/api/institucional/info'), api.get('/api/contenidos/noticias'), api.get('/api/contenidos/eventos')])
  if (respuestas[0].status === 'fulfilled' && respuestas[0].value.data) info.value = respuestas[0].value.data
  if (respuestas[1].status === 'fulfilled') noticias.value = respuestas[1].value.data || []
  if (respuestas[2].status === 'fulfilled') eventos.value = respuestas[2].value.data || []
})
</script>

<style scoped>
:global(body) { background: #fff; color: #20242b; font-family: 'Trebuchet MS', 'Segoe UI', sans-serif; }
.home-shell { --red: #a71930; --deep-red: #721426; --ink: #20242b; --cream: #f7f3ef; overflow: hidden; }
.section-wrap, .header-inner { width: min(1120px, calc(100% - 40px)); margin: auto; }
.site-header { background: #fff; border-bottom: 1px solid #eee8e4; position: sticky; top: 0; z-index: 10; }
.header-inner { min-height: 82px; display: flex; align-items: center; justify-content: space-between; gap: 30px; }
.brand { display: inline-flex; align-items: center; gap: 11px; color: var(--ink); text-decoration: none; letter-spacing: .01em; }
.brand strong { display: block; font-size: 15px; letter-spacing: .02em; }.brand small { display: block; color: #8d8581; font-size: 10px; margin-top: 2px; text-transform: uppercase; letter-spacing: .12em; }
.brand-mark { width: 42px; height: 42px; display: grid; place-items: center; background: var(--red); color: #fff; font-size: 11px; font-weight: bold; letter-spacing: .06em; }
.site-nav { display: flex; align-items: center; gap: 28px; font-size: 13px; }.site-nav a { color: #4d5056; text-decoration: none; }.site-nav a:hover { color: var(--red); }.nav-action { border: 1px solid var(--red); color: var(--red) !important; padding: 10px 15px; }.menu-button { display: none; border: 0; background: transparent; font-size: 23px; color: var(--red); }
.hero { min-height: 580px; background: var(--deep-red); color: white; position: relative; display: flex; align-items: center; padding: 80px max(20px, calc((100% - 1120px) / 2)); isolation: isolate; }.hero::after { content: ''; position: absolute; z-index: -1; right: 5%; top: 14%; width: 360px; height: 360px; border: 1px solid #d8a3ab66; transform: rotate(45deg); box-shadow: 0 0 0 34px #ffffff08, 0 0 0 68px #ffffff05; }.hero-copy { max-width: 650px; }.eyebrow, .section-kicker { text-transform: uppercase; letter-spacing: .19em; font-size: 11px; font-weight: bold; color: #d8a3ab; margin: 0 0 21px; }.hero h1 { font-family: Georgia, serif; font-size: clamp(43px, 7vw, 82px); line-height: .98; letter-spacing: 0; margin: 0 0 28px; max-width: 730px; }.hero-lead { font-size: 18px; line-height: 1.6; max-width: 480px; color: #f6dfe2; margin: 0 0 35px; }.hero-actions { display: flex; align-items: center; gap: 25px; }.primary-button { background: #fff; color: var(--red); padding: 14px 18px; text-decoration: none; font-weight: bold; font-size: 13px; }.primary-button span { margin-left: 20px; }.text-link { color: #fff; text-decoration: none; font-size: 13px; border-bottom: 1px solid #d8a3ab; padding-bottom: 4px; }.hero-note { position: absolute; right: max(20px, calc((100% - 1120px) / 2)); bottom: 45px; border-left: 1px solid #d8a3ab; padding-left: 16px; color: #f6dfe2; font-size: 12px; line-height: 1.4; }.hero-note span { color: #e9b4bd; font-size: 11px; }
.intro-section { padding: 105px 0 95px; }.intro-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 80px; }.intro-section h2, .section-heading h2 { font-family: Georgia, serif; font-size: clamp(36px, 5vw, 58px); line-height: 1.06; margin: 0; }.intro-section h2 em { color: var(--red); font-style: normal; }.intro-text { font-size: 18px; line-height: 1.75; color: #65676c; margin: 5px 0 24px; }.inline-link { color: var(--red); font-weight: bold; font-size: 13px; text-decoration: none; }.inline-link span { padding-left: 13px; }
.purpose-section { background: var(--cream); border-top: 1px solid #eee3de; border-bottom: 1px solid #eee3de; }.purpose-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 45px; padding: 70px 0; }.purpose-grid article { border-top: 2px solid var(--red); padding-top: 17px; }.number { font-size: 11px; color: var(--red); letter-spacing: .1em; }.purpose-grid h3 { font-family: Georgia, serif; font-size: 27px; margin: 24px 0 12px; }.purpose-grid p { color: #666; line-height: 1.7; margin: 0; font-size: 14px; }
.content-section { padding: 100px 0; }.section-heading { display: flex; align-items: center; gap: 28px; margin-bottom: 42px; }.section-heading .section-kicker { color: var(--red); margin-bottom: 13px; }.heading-line { height: 1px; background: #ddd2cd; flex: 1; }.news-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 18px; }.news-card { min-height: 230px; border: 1px solid #e9e3df; padding: 26px; position: relative; transition: transform .2s, box-shadow .2s; }.news-card:hover { transform: translateY(-4px); box-shadow: 0 12px 25px #54101d12; }.news-date { color: var(--red); font-size: 11px; text-transform: uppercase; letter-spacing: .1em; }.news-card h3 { font-family: Georgia, serif; font-size: 23px; line-height: 1.2; margin: 22px 0 12px; }.news-card p { color: #74747a; font-size: 13px; line-height: 1.6; margin: 0; }.card-arrow { color: var(--red); font-size: 20px; position: absolute; right: 22px; bottom: 17px; }.empty-state { background: var(--cream); color: #777; padding: 25px; margin: 0; }
.agenda-section { background: var(--red); color: #fff; padding: 85px 0; }.light .section-kicker { color: #eab4bd; }.light h2 { color: #fff; }.event-list article { display: flex; align-items: center; gap: 25px; padding: 20px 0; border-top: 1px solid #c87582; position: relative; }.event-list time { width: 62px; text-align: center; border-right: 1px solid #d68f99; padding-right: 20px; }.event-list time strong { display: block; font-family: Georgia, serif; font-size: 31px; }.event-list time span { color: #f2cdd2; font-size: 11px; letter-spacing: .08em; }.event-list h3 { margin: 0 0 5px; font-family: Georgia, serif; font-size: 21px; }.event-list p { margin: 0; color: #f0c9ce; font-size: 13px; }.event-list .card-arrow { color: #fff; bottom: auto; top: 31px; }.light-text { color: #f0c9ce; }
.site-footer { background: #25252a; color: #fff; padding: 28px 0; }.footer-inner { display: flex; align-items: center; justify-content: space-between; gap: 20px; }.footer-brand { color: #fff; }.footer-inner p { color: #aaa; font-size: 13px; }.footer-login { color: #f2cdd2; text-decoration: none; font-size: 13px; }
.contact-section { background: #f7f3ef; padding: 80px 0; }.contact-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 80px; }.contact-section h2 { font-family: Georgia, serif; font-size: 54px; margin: 0; }.contact-section p { color: #65676c; line-height: 1.7; margin: 0 0 12px; }.contact-section a { color: var(--red); }
@media (max-width: 700px) { .section-wrap, .header-inner { width: min(100% - 28px, 1120px); }.header-inner { min-height: 70px; }.menu-button { display: block; }.site-nav { display: none; position: absolute; top: 70px; left: 0; right: 0; background: #fff; padding: 18px 14px; box-shadow: 0 8px 15px #00000012; flex-direction: column; align-items: stretch; gap: 16px; }.site-nav.open { display: flex; }.nav-action { text-align: center; }.hero { min-height: 600px; padding: 80px 24px; }.hero::after { right: 2%; top: 52%; width: 190px; height: 190px; }.hero-note { right: 24px; bottom: 24px; }.hero-lead { font-size: 16px; }.hero-actions { align-items: flex-start; flex-direction: column; gap: 18px; }.intro-section, .content-section { padding: 70px 0; }.intro-grid, .purpose-grid, .news-grid, .contact-grid { grid-template-columns: 1fr; gap: 30px; }.purpose-grid { padding: 55px 0; }.section-heading { align-items: flex-start; }.heading-line { display: none; }.footer-inner { align-items: flex-start; flex-direction: column; }.event-list article { gap: 15px; }.event-list time { width: 52px; padding-right: 12px; }.event-list h3 { font-size: 18px; }.contact-section h2 { font-size: 42px; } }
</style>