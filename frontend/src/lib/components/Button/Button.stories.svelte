<script module>
  import { defineMeta } from '@storybook/addon-svelte-csf';
  import Button from './Button.svelte';
  import { fn } from 'storybook/test';
	import Icon from '$components/Icon/Icon.svelte';

  // More on how to set up stories at: https://storybook.js.org/docs/writing-stories
  const { Story } = defineMeta({
    title: 'lib/Button',
    component: Button,
    tags: ['autodocs'],
    argTypes: {
      children: { control: 'text' },
      onclick: { action: 'clicked' },
      class: { control: 'text' }
    },
    args: {
      onclick: fn(),
    }
  });
</script>

<!-- More on writing stories with args: https://storybook.js.org/docs/writing-stories/args -->
<Story name="Primary" args={{ variant: 'primary' }}>
  <p>Button</p>
</Story>

<Story name="Secondary" args={{ variant: 'secondary' }}>
  <p>Button</p>
</Story>

<Story name="Success" args={{ variant: 'success' }}>
  <p>Button</p>
</Story>

<Story name="Destructive" args={{ variant: 'destructive' }}>
  <p>Button</p>
</Story>

<Story name="Icon" args={{ variant: 'primary', class: 'rounded-full p-2' }}>
  <Icon name="edit" />
</Story>

<Story name="Icon2" args={{ variant: 'primary', class: 'rounded-full p-2' }}>
  <Icon name="right-arrow" />
</Story>

<Story name="Skeleton" args={{ variant: 'primary', skeleton: true }}>
  <p>Button</p>
</Story>

<Story name="Disabled" args={{ variant: 'primary', disabled: true }}>
  <p>Button</p>
</Story>
