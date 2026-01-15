import adapter from '@sveltejs/adapter-static';
import { vitePreprocess } from '@sveltejs/vite-plugin-svelte';

/** @type {import('@sveltejs/kit').Config} */
const config = {
	// Consult https://svelte.dev/docs/kit/integrations
	// for more information about preprocessors
	preprocess: vitePreprocess(),
	compilerOptions: {
		runes: true
	},

	vitePlugin: {
		dynamicCompileOptions({ filename }) {
			if (filename.includes('node_modules')) {
				return {
					runes: undefined
				};
			}
		}
	},

	kit: {
		// adapter-auto only supports some environments, see https://svelte.dev/docs/kit/adapter-auto for a list.
		// If your environment is not supported, or you settled on a specific environment, switch out the adapter.
		// See https://svelte.dev/docs/kit/adapters for more information about adapters.
		adapter: adapter({
			fallback: 'index.html',
		}),
		alias: {
			$assets: 'src/lib/assets',
			$components: 'src/lib/components',
			$modules: 'src/lib/modules',
			$stores: 'src/lib/stores',
			$types: 'src/lib/types',
		},
		paths: {
			base: '/paw-2025a-15',
		},
		version: {
			name: (+Date.now()).toString(36),
		}
	}
};

export default config;
