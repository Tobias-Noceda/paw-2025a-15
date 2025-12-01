<script module>
  import { defineMeta } from '@storybook/addon-svelte-csf';
  import Divider from './Divider.svelte';

  // More on how to set up stories at: https://storybook.js.org/docs/writing-stories
  const { Story } = defineMeta({
    title: 'lib/Divider',
    component: Divider,
    tags: ['autodocs'],
    argTypes: {
      size: { control: { type: 'select', options: ['sm', 'md', 'lg', 'auto'] } },
    },
  });
</script>

<!-- More on writing stories with args: https://storybook.js.org/docs/writing-stories/args -->
<Story name="Small" args={{ size: 'sm' }} />

<Story name="Medium" args={{ size: 'md' }} />

<Story name="Large" args={{ size: 'lg' }} />

<Story name="Auto" args={{ size: 'auto' }} />
