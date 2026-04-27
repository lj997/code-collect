import hljs from 'highlight.js'

export default {
  mounted(el, binding) {
    const code = el.querySelector('code')
    if (code) {
      const language = binding.value || el.getAttribute('data-language')
      if (language) {
        try {
          hljs.highlightElement(code)
        } catch (e) {
          console.error('Highlight error:', e)
        }
      }
    }
  },
  updated(el, binding) {
    const code = el.querySelector('code')
    if (code) {
      const language = binding.value || el.getAttribute('data-language')
      if (language) {
        try {
          code.removeAttribute('data-highlighted')
          hljs.highlightElement(code)
        } catch (e) {
          console.error('Highlight error:', e)
        }
      }
    }
  }
}
