<script module lang="ts">
  import { defineMeta } from '@storybook/addon-svelte-csf';
  import RadioCheck from './RadioCheck.svelte';
  import { fn } from 'storybook/test';

  let options = $state([
    { id: 'option1', label: 'Option 1', checked: false },
    { id: 'option2', label: 'Option 2', checked: true },
    { id: 'option3', label: 'Option 3', checked: false },
  ]);

  const { Story } = defineMeta({
    title: 'lib/RadioCheck',
    component: RadioCheck,
    tags: ['autodocs'],
    argTypes: {
      disabled: { control: 'boolean' },
      containerClass: { control: 'text' },
    },
    args: {
      options,
      onchange: fn(),
    }
  });
</script>

<Story name="Default" args={{ }} />

<Story name="With Skeleton" args={{ skeleton: true }} />

<Story name="Disabled" args={{ disabled: true }} />

<Story name="Vertical Layout" args={{ containerClass: 'flex flex-col w-fit gap-2' }} />
