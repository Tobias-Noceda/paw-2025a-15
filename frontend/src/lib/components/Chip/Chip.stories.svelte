<script module lang="ts">
  import { defineMeta } from '@storybook/addon-svelte-csf';
  import Chip from './Chip.svelte';

  // More on how to set up stories at: https://storybook.js.org/docs/writing-stories
  const { Story } = defineMeta({
    title: 'lib/Chip',
    component: Chip,
    tags: ['autodocs'],
    argTypes: {
      label: { control: 'text' },
      variant: { control: { type: 'select', options: ['primary', 'secondary', 'tertiary', 'success', 'destructive'] } },
      class: { control: 'text' }
    },
    args: {
      label: 'Chip'
    }
  });
</script>

<!-- More on writing stories with args: https://storybook.js.org/docs/writing-stories/args -->
<Story name="Primary" args={{ variant: 'primary' }} />

<Story name="Secondary" args={{ variant: 'secondary' }} />

<Story name="Tertiary" args={{ variant: 'tertiary' }} />

<Story name="Success" args={{ variant: 'success' }} />

<Story name="Destructive" args={{ variant: 'destructive' }} />
