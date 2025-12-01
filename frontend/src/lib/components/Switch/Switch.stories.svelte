<script module>
  import { defineMeta } from '@storybook/addon-svelte-csf';
  import Switch from './Switch.svelte';
  import { fn } from 'storybook/test';

  // More on how to set up stories at: https://storybook.js.org/docs/writing-stories
  const { Story } = defineMeta({
    title: 'lib/Switch',
    component: Switch,
    tags: ['autodocs'],
    argTypes: {
      disabled: { control: 'boolean' },
      skeleton: { control: 'boolean' },
      class: { control: 'text' },
      onchange: { action: 'toggled' }
    },
    args: {
      class: 'w-10! h-5!',
      onchange: fn(),
    }
  });
</script>

<!-- More on writing stories with args: https://storybook.js.org/docs/writing-stories/args -->
<Story name="Default" args={{}} />

<Story name="Disabled" args={{ disabled: true }} />

<Story name="Skeleton" args={{ skeleton: true }} />
