import type { Preview } from '@storybook/sveltekit';
import '../src/routes/layout.css'; // or wherever your Tailwind CSS is imported

const preview: Preview = {
  parameters: {
    controls: {
      matchers: {
       color: /(background|color)$/i,
       date: /Date$/i,
      },
    },
  },
};

export default preview;