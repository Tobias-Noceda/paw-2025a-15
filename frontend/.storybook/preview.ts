import type { Preview, Decorator } from '@storybook/sveltekit';
import '../src/routes/layout.css'; // or wherever your Tailwind CSS is imported
import { setLocale, locales } from '../src/lib/paraglide/runtime.js';

// Decorator to handle locale changes
const withLocale: Decorator = (story, context) => {
  const locale = context.globals.locale;
  if (locale && locales.includes(locale)) {
    setLocale(locale, { reload: true });
  }
  return story();
};

const preview: Preview = {
  decorators: [withLocale],
  parameters: {
    controls: {
      matchers: {
       color: /(background|color)$/i,
       date: /Date$/i,
      },
    },
  },
  globalTypes: {
    locale: {
      description: 'Internationalization locale',
      defaultValue: 'es',
      toolbar: {
        icon: 'globe',
        items: locales.map((locale) => ({
          value: locale,
          title: locale.toUpperCase(),
        })),
        showName: true,
        dynamicTitle: true,
      },
    },
  },
  initialGlobals: {
    locale: 'es',
  },
};

export default preview;